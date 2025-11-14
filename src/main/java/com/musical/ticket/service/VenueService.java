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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final FileService fileService; // 👈 추가

    /*
     * (Admin) 공연장 및 좌석 탬플릿 등록(C)
     */
    @Transactional
    public VenueResDto saveVenue(VenueSaveReqDto reqDto, MultipartFile layoutImage) { // 👈 파라미터 추가

        // 0. 이미지 저장 (있으면)
        if (layoutImage != null && !layoutImage.isEmpty()) {
            String imageUrl = fileService.saveFile(layoutImage, "venue-layouts");
            reqDto.setLayoutImageUrl(imageUrl); // DTO에 이미지 URL 설정
        }

        // 1. 공연장(Venue) 정보만 먼저 저장
        Venue venue = reqDto.toEntity();
        Venue savedVenue = venueRepository.save(venue);

        // 2. 좌석 템플릿(Seat) DTO 리스트를 Entity리스트로 변환
        List<Seat> seats = reqDto.getSeats().stream()
                .map(seatDto -> seatDto.toEntity(savedVenue))
                .collect(Collectors.toList());

        // 3. 좌석 템플릿(Seat) 리스트를 DB에 일괄 저장
        seatRepository.saveAll(seats);

        // 4. 저장된 Venue를 (좌석 정보 포함) 다시 조회하여 반환
        Venue fullySavedVenue = venueRepository.findById(savedVenue.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        return new VenueResDto(fullySavedVenue);
    }

    /*
     * (All) 공연장 전체 목록 조회(R)
     */
    public List<VenueResDto> getAllVenues() {
        List<Venue> venues = venueRepository.findAll();
        return venues.stream()
                .map(VenueResDto::new)
                .collect(Collectors.toList());
    }

    /*
     * (All) 공연장 상세 조회(R)
     */
    public VenueResDto getVenueById(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
        return new VenueResDto(venue);
    }

    /*
     * (Admin) 공연장 수정(U)
     */
    @Transactional
public VenueResDto updateVenue(Long venueId, VenueSaveReqDto reqDto, MultipartFile layoutImage){
    
    // 1. 기존 공연장 조회
    Venue venue = venueRepository.findById(venueId)
        .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
    
    // 2. 이미지 처리
    String imageUrl = venue.getLayoutImageUrl();
    if (layoutImage != null && !layoutImage.isEmpty()) {
        if (venue.getLayoutImageUrl() != null) {
            fileService.deleteFile(venue.getLayoutImageUrl());
        }
        imageUrl = fileService.saveFile(layoutImage, "venue-layouts");
    }
    
    // 3. 공연장 정보 업데이트 (엔티티 메서드 사용)
    venue.updateInfo(reqDto.getName(), reqDto.getLocation(), imageUrl);
    
    // 4. 기존 좌석 삭제
    seatRepository.deleteByVenueId(venueId);
    
    // 5. 새 좌석 등록
    List<Seat> seats = reqDto.getSeats().stream()
        .map(seatDto -> seatDto.toEntity(venue))
        .collect(Collectors.toList());
    
    seatRepository.saveAll(seats);
    
    // 6. @Transactional이 끝나면 자동으로 flush되므로 다시 조회
    Venue updatedVenue = venueRepository.findById(venueId)
        .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    
    return new VenueResDto(updatedVenue);
}
}