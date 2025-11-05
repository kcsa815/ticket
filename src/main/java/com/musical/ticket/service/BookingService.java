package com.musical.ticket.service;
/*
 * 예매 로직의 총사령관
 * @Transactional : 이 메서드 전체가 하나의 작업단위가 됨.
    * 1. 좌석 락, 2. 좌석 검증, 3. Booking 저장, 4. PerformanceSeat업데이트
    * 이 중 하나라도 실패하면, 락이 풀리고 모든 작업이 rollback됨.
 * @SecurityUtil.getCurrentUserEmail() : Util을 사용하여 토큰에 사용자 이메일을 가져옴
 * userRepository.findByEmail(...)  :이메일로 User 엔티티 조회
 * performanceSeatRepository.findAllByIdWithPessimisticLock(...) : 2번에서 만든 락킹 쿼리를 호출하여 좌석을 *선점*
 */

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musical.ticket.domain.entity.Booking;
import com.musical.ticket.domain.entity.Performance;
import com.musical.ticket.domain.entity.PerformanceSeat;
import com.musical.ticket.domain.entity.User;
import com.musical.ticket.domain.enums.BookingStatus;
import com.musical.ticket.dto.booking.BookingReqDto;
import com.musical.ticket.dto.booking.BookingResDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.BookingRepository;
import com.musical.ticket.repository.PerformanceSeatRepository;
import com.musical.ticket.repository.UserRepository;
import com.musical.ticket.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final UserRepository userRepository;

    /*
     * (User) 좌석 예매(CREATE) - 동시성 제어를 위해 비관적 락 사용
     * @param reqDto(*예매할 공연 좌석 ID* 리스트)
     */
    @Transactional 
    public BookingResDto createBooking(BookingReqDto reqDto){
        
        //1. 현재 로그인한 사용자 Entity조회
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Long> seatIds = reqDto.getPerformanceSeatIds();
        if(seatIds.isEmpty()){
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        //2. 요청된 좌석 ID로 *비관적 락*을 걸어 조회
        List<PerformanceSeat> seats = performanceSeatRepository.findAllByIdWithPessimisticLock(seatIds);

        /*---------3. 검증(Validation)----------*/
        
        //3-1. 요청된 ID의 좌석이 DB에 모두 존재하는지 확인
        if(seats.size() != seatIds.size()){
            log.warn("요청된 좌석 ID중 일부가 존재하지 않습니다.");
            throw new CustomException(ErrorCode.SEAT_NOT_FOUND);
        }
        Performance performance = null; //좌석들이 모두 동일한 공연 회차인지 검증
        int totalPrice = 0;

        for(PerformanceSeat seat : seats){
            //3-2. 락을 잡고 보니, 이미 예약된 좌석인지 확인
            if(seat.getIsReserved()){
                log.warn("동시성 문제 : 좌석 {}는 이미 예약되었습니다.", seat.getId());
                throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
            }
            //3-3. 모든 좌석이 동일한 공연(Performance)에 속하는지 확인
            if(performance ==null){
                performance = seat.getPerformance();
            }else if(!performance.getId().equals(seat.getPerformance().getId())){
                log.warn("서로 다른 공연 회차의 좌석을 동시에 예매할 수 없습니다.");
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }
            //3-4. 총 가격 계산
            totalPrice += seat.getPrice();
        }               

        /*---------4. 예매(Booking) 생성 및 저장----------*/

        //4-1. Booking엔티티 생성
        Booking booking = Booking.builder()
            .user(user)
            .performance(performance)
            .bookingStatus(BookingStatus.COMPLETED)
            .totalPrice(totalPrice)
            .build();

        //4-2. Booking저장(먼저 저장해서 bookind_id확보)
        Booking savedBooking = bookingRepository.save(booking);

        /*---------5. 좌석(PerformanceSeat)상태 업데이트----------*/

        //5-1. 락을 잡았던 좌석들에 예매 정보(booking_id)및 상태(isReserved)업데이트
        for(PerformanceSeat seat : seats){
            seat.setBooking(savedBooking);
        }
        log.info("예매 성공 (Booking_ID : {})", savedBooking.getId());

        //6. 결과 반환(dto변환)
        return new BookingResDto(savedBooking);
    }

    /*
     * (User) 내 예매 내역 목록 조회(R)
     */
    public List<BookingResDto> getMyBookings(){
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Booking> bookings = bookingRepository.findByUserOrderByCreatedAtDesc(user);

        return bookings.stream()
            .map(BookingResDto::new)
            .collect(Collectors.toList());
    }
    
}
