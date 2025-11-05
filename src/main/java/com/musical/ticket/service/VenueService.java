package com.musical.ticket.service;
// Venue(공연장) API의 핵심 두뇌
// @RequireArgsConstuctor:final로 선언된 venueRepository, seatRepository를 생성자 주입(DI)으로 받아옴
// @Transactional : saveVenue 메서드에 이 어노테이션을 붙임
    // 이 메서드는 2가지 작업(1. 공연장 저장, 2.좌석 N개 저장)을 함
    // 만약 1. 공연장 저장은 성공했는데, 2. 50번째 좌석 지정 도중 오류가 난다면?
    // @Transactional이 없다면 공연장만 덩그러니 저장되고 좌석 49개만 저장되는 등 데이터가 오염됨.
    // @Transactional을 부이면, 이 메서드 전체가 하나의 작업 단위로 묶임. 좌석 저장 50번째에서 오류가 났다면 성공했던 1. 공연장, 2. 49개의 좌석 저장이 모두 취소(Rollback)됨.
    // 이를 통해 데이터의 일관성을 보장할 수 있음.

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.dto.venue.VenueResDto;
import com.musical.ticket.dto.venue.VenueSaveReqDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.SeatRepository;
import com.musical.ticket.repository.VenueRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //기본은 읽기 전용
public class VenueService {

    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;

    //(Admin) 공연장 및 좌석 탬플릿 등록(C)
    @Transactional //쓰기작업이므로 readOnly = false
    public VenueResDto saveVenue(VenueSaveReqDto reqDto){

        // 1. 공연장(Venue) 정보만 먼저 저장
        Venue venue = reqDto.toEntity();
        Venue savedVenud = venueRepository.save(venue);

        // 2. 좌석 템플릿(Seat) DTO 리스트를 Entity리스트로 변환
        // 이때 1번에서 저장된 Venue객체를 주입하여 연관관계 설정
        List<Seat> seats = reqDto.getSeats().stream()
            .map(seatDto -> seatDto.toEntity(savedVenud))
            .collect(Collectors.toList());

        // 3. 좌석 템플릿(Seat) 리스트를 DB에 일괄 저장(Batch Insert)
        seatRepository.saveAll(seats);

        // 4. 저장된 Venue를 (좌석 정보 포함) 다시 조회하여 반환
        // Jpa Cascade 설정이나 양방향 편의 메서드 설정에 따라 이 부분은 최적화 가능
        Venue fullySavedVenue = venueRepository.findById(savedVenud.getId())
            .orElseThrow(()->new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        return new VenueResDto(fullySavedVenue);
    }

    //(All) 공연장 전체 목록 조회(R)
    public List<VenueResDto> getAllVenues(){
        List<Venue> venues = venueRepository.findAll();
        return venues.stream()
            .map(VenueResDto::new) //venue ->new VenueResDto(venue)
            .collect(Collectors.toList());
    }

    //(All) 공연장 상세 조회(R)
    public VenueResDto getVenueById(Long venueId){
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
        return new VenueResDto(venue);
    }
    
}
