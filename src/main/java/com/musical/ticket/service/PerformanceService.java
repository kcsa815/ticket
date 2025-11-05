package com.musical.ticket.service;
// 가장 복잡하고 중요한 비지니스 로직
    //@Transactional : savePerformance 메서드 전체를 하나의 거대한 트랜잭션으로 묶음
        //1. Performance 1개 저장
        //2. PerformanceSeat 1000개(예시) 저장을 순차적으로 수행
        // if 1이 성공하고 2 처리중 오류가 발생하게 되면 처리하다 만 "망가진 데이터"가 DB에 남게 됨.
        // @Transactional이 있으므로 2가 실패하는 순간 1까지 모두 Rollback되어 DB는 시작 전의 상태로 돌아감 -> 데이터 무결성 지킬 수 있음
    //savePerformance 로직 흐름 : 
        // 1. 엔티티 조회(Validation) : musicalId, venueId로 Musical, Venue엔티티를 조회함 ->없다면 CustomExeption(404) 발생시킴
        // 2. Performance 저장 : DTO의 performanceDate와 조회한 엔티티들로 Performance객체를 만들어 저장함. Jpa가 이 객체에 Performance_id를 채워줌
        // 3. 좌석 템플릿 로드 : venue.getStatus()를 통해 이 공연장의 모든 Seat(좌석 템플릿) 리소스를 가져옴
        // 4. PerformanceSeat 생성 루프 : 템플릿 리스트(List<Seat>)를 for문으로 처리함
            // seatTemplate.getSeatGrade(): 템플릿에서 '등급'을 가져옴
            // priceByGrade.get(grade):DTO로 받은 Map에서 등급의 가격을 조회함.
            // PerformanceSeat.builder()...build(): savedPerformance, seatTemplate, price를 조합하여 '실제 예매 좌석'(PerformanceSeat)엔티티를 생성함
        // 5. 일괄 저장(Bulk Insert): performanceSeatRepository.saveAll()을 호출하여 PerformanceSeat리스트 전체를 한 번의 쿼리로 DB에 저장함
    //getPerformanceDetails로직 : findById로 Performance를 조회함.
    //getPerformancesByMusical로직 : Repository에 만든 findByMusicalId쿼리를 호출 ->PerformanceSimpleResDto로 변환해서 반환.

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.musical.ticket.domain.entity.Musical;
import com.musical.ticket.domain.entity.Performance;
import com.musical.ticket.domain.entity.PerformanceSeat;
import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.domain.enums.SeatGrade;
import com.musical.ticket.dto.performance.PerformanceResDto;
import com.musical.ticket.dto.performance.PerformanceSaveReqDto;
import com.musical.ticket.dto.performance.PerformanceSimpleResDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.MusicalRepository;
import com.musical.ticket.repository.PerformanceRepository;
import com.musical.ticket.repository.PerformanceSeatRepository;
import com.musical.ticket.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //읽기 전용
public class PerformanceService {
    
    private final PerformanceRepository performanceRepository;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final MusicalRepository musicalRepository;
    private final VenueRepository venueRepository;

    // (Admin) 공연 회차 등록(C)
    // PerformanceSeat를 '가격'과 함께 자동으로 생성함.
    // @param reqDto(musicalId, venueId, date, pricesByGrade)
    @Transactional
    public Long savePerformance(PerformanceSaveReqDto reqDto){
        //1. 뮤지컬 엔티티 조회(없으면 404)
        Musical musical = musicalRepository.findById(reqDto.getMusicalId())
            .orElseThrow(()-> new CustomException(ErrorCode.MUSICAL_NOT_FOUND));

        //2. 공연장 엔티티 조회(없으면 404)
        Venue venue = venueRepository.findById(reqDto.getVenueId())
            .orElseThrow(()-> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        // 3. 공연회차(Performance) 엔티티 생성 및 저장(먼저 저장해 ID확보)
        Performance performance = reqDto.toEntity(musical, venue);
        Performance savedPerformance = performanceRepository.save(performance);
        log.info("공연 회차(Performance_ID:{})가 생성되었습니다.", savedPerformance.getId());

        //4. 공연장의 좌석 템플릿(List<Seat>)을 모두 가져옴
        List<Seat> seatTemplates = venue.getSeats();
        if(seatTemplates.isEmpty()){
            //방어코드 - 좌석이 없는 공연장은 등록 불가
            log.warn("Venue_ID: {} 에 좌석 템플릿이 없습니다.", venue.getId());
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        //dto로 받은 등급별 가격표
        Map<SeatGrade, Integer> pricesByGrade = reqDto.getPricesByGrade();

        //5. 좌석 템플릿 ->PerformanceSeat엔티티로 변환(Bulk Insert준비)
        List<PerformanceSeat> performanceSeats = new ArrayList<>();
        for(Seat seatTemplate : seatTemplates){
            //5-1. 템플릿의 등급을 가져옴
            SeatGrade grade = seatTemplate.getSeatGrade();

            //5-2. dto의 가격표(Map)에서 해당 등급의 가격을 조회
            Integer price = pricesByGrade.get(grade);
            if(price == null){
                //5-2-1. 가격이 빠뜨린 경우
                log.error("등급 {}에 대한 가격이 설정되지 않았습니다.", grade);
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            //5-3. 실제 예매 대상이 될 PerformanceSeat생성
            PerformanceSeat performanceSeat = PerformanceSeat.builder()
                .performance(savedPerformance)
                .seat(seatTemplate)
                .price(price)
                .isReserved(false)  //기본값은 "예약 가능"
                .build();
            performanceSeats.add(performanceSeat);
        }

        //6. PerformanceSeat리스트를 DB에 일괄 저장
        performanceSeatRepository.saveAll(performanceSeats);
        log.info("{}개의 좌석(PerformanceSeat)이 공연 회차에 맞게 생성되었습니다.", performanceSeats.size());
    
        return savedPerformance.getId();
    }

    //(All) 공연 회차 상세 조회(R)
    //공연정보 + 전체 좌석 맵(PerformanceSeat)포함
    public PerformanceResDto getPerformanceDetails(Long performanceId){
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(()->new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        return new PerformanceResDto(performance);
    }

    //(All) 특정 뮤지컬의 모든 공연 회차 목록 조회(R)
    //좌석 맵 제외, 간략한 정보만
    public List<PerformanceSimpleResDto> getPerformanceByMusical(Long musicalId){
        //뮤지컬이 존재하는지 먼저 확인
        if (!musicalRepository.existsById(musicalId)){
            throw new CustomException(ErrorCode.MUSICAL_NOT_FOUND);
        }

        //Repository의 커스텀 쿼리 호출
        List<Performance> performances = performanceRepository.findByMusicalId(musicalId);

        //Simple DTO로 변환하여 반환
        return performances.stream()
            .map(PerformanceSimpleResDto::new)
            .collect(Collectors.toList());
    }
}