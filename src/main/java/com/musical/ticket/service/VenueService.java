package com.musical.ticket.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; 
import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.dto.venue.VenueResDto;
import com.musical.ticket.dto.venue.VenueSaveReqDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.SeatRepository;
import com.musical.ticket.repository.VenueRepository;
import com.musical.ticket.util.FileUtil; 
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final FileUtil fileUtil;

    //(Admin) 공연장 등록(C)
    @Transactional
    public VenueResDto saveVenue(VenueSaveReqDto reqDto, MultipartFile layoutImage){ 
        
        System.out.println("========================================");
        System.out.println("=== saveVenue 시작!");
        System.out.println("=== 공연장 이름: " + reqDto.getName());
        System.out.println("=== 공연장 위치: " + reqDto.getLocation());
        System.out.println("=== 지역: " + reqDto.getRegion());
        System.out.println("=== 좌석 개수: " + (reqDto.getSeats() != null ? reqDto.getSeats().size() : 0));
        System.out.println("=== 이미지 있음?: " + (layoutImage != null && !layoutImage.isEmpty()));
        System.out.println("========================================");
        
        try {
            // 1. 좌석 배치도 이미지 저장
            System.out.println(">>> 1단계: 이미지 저장 시작");
            String layoutImageUrl = fileUtil.saveFile(layoutImage);
            System.out.println(">>> 1단계 완료: " + layoutImageUrl);

            // 2. 공연장(Venue) 정보 저장 (이미지 URL 포함)
            System.out.println(">>> 2단계: Venue 엔티티 생성 및 저장");
            Venue venue = reqDto.toEntity(layoutImageUrl); 
            Venue savedVenue = venueRepository.save(venue);
            System.out.println(">>> 2단계 완료: Venue ID = " + savedVenue.getId());

            // 3. 좌석 템플릿(Seat) DTO -> Entity 변환 및 저장
            System.out.println(">>> 3단계: 좌석 저장 시작");
            List<Seat> seats = reqDto.getSeats().stream()
                    .map(seatDto -> seatDto.toEntity(savedVenue))
                    .collect(Collectors.toList());
            seatRepository.saveAll(seats);
            System.out.println(">>> 3단계 완료: " + seats.size() + "개 좌석 저장");

            // 4. 저장된 Venue 반환 (N+1 방지)
            System.out.println(">>> 4단계: VenueResDto 생성");
            VenueResDto result = new VenueResDto(savedVenue, seats);
            
            System.out.println("========================================");
            System.out.println("=== saveVenue 성공!");
            System.out.println("========================================");
            
            return result;
            
        } catch (Exception e) {
            System.out.println("!!! 에러 발생 !!!");
            System.out.println("!!! 에러 타입: " + e.getClass().getName());
            System.out.println("!!! 에러 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    //(Admin) 공연장 수정(U)
    @Transactional
    public VenueResDto updateVenue(Long venueId, VenueSaveReqDto reqDto, MultipartFile layoutImage) {
        
        System.out.println("========================================");
        System.out.println("=== updateVenue 시작! venueId: " + venueId);
        System.out.println("========================================");
        
        try {
            System.out.println(">>> 1단계: 공연장 조회");
            Venue venue = venueRepository.findByIdWithFetch(venueId) 
                     .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
            System.out.println(">>> 1단계 완료: " + venue.getName());

            // 1. 새 배치도 이미지 처리
            System.out.println(">>> 2단계: 이미지 처리 시작");
            String newLayoutImageUrl = venue.getLayoutImageUrl();
            if (layoutImage != null && !layoutImage.isEmpty()) {
                fileUtil.deleteFile(venue.getLayoutImageUrl());
                newLayoutImageUrl = fileUtil.saveFile(layoutImage);
                System.out.println(">>> 새 이미지 저장: " + newLayoutImageUrl);
            }
            System.out.println(">>> 2단계 완료");
            
            // 2. 공연장 정보 업데이트 (Dirty Checking)
            System.out.println(">>> 3단계: 공연장 정보 업데이트");
            venue.update(reqDto.getName(), reqDto.getLocation(), newLayoutImageUrl, reqDto.getRegion());
            System.out.println(">>> 3단계 완료");
            
            // 3. 기존 좌석 템플릿은 모두 삭제
            System.out.println(">>> 4단계: 기존 좌석 삭제");
            seatRepository.deleteAll(venue.getSeats());
            venue.getSeats().clear();
            System.out.println(">>> 4단계 완료");

            // 4. 새 좌석 템플릿 리스트 생성 및 저장
            System.out.println(">>> 5단계: 새 좌석 저장");
            List<Seat> newSeats = reqDto.getSeats().stream()
                    .map(seatDto -> seatDto.toEntity(venue))
                    .collect(Collectors.toList());
            seatRepository.saveAll(newSeats);
            System.out.println(">>> 5단계 완료: " + newSeats.size() + "개 좌석 저장");

            // 5. 업데이트된 정보 반환
            System.out.println("========================================");
            System.out.println("=== updateVenue 성공!");
            System.out.println("========================================");
            
            return new VenueResDto(venue, newSeats);
            
        } catch (Exception e) {
            System.out.println("!!! 에러 발생 !!!");
            System.out.println("!!! 에러 타입: " + e.getClass().getName());
            System.out.println("!!! 에러 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    //(All) 공연장 전체 목록 조회(R)
    public List<VenueResDto> getAllVenues(){
        List<Venue> venues = venueRepository.findAll();
        return venues.stream()
                .map(VenueResDto::new)
                .collect(Collectors.toList());
    }

    //(All) 공연장 상세 조회(R)
    public VenueResDto getVenueById(Long venueId){
        Venue venue = venueRepository.findByIdWithFetch(venueId)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
        return new VenueResDto(venue);
    }
}