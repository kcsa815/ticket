package com.musical.ticket.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // 👈 [추가!]
import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.dto.venue.VenueResDto;
import com.musical.ticket.dto.venue.VenueSaveReqDto;
import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;
import com.musical.ticket.repository.SeatRepository;
import com.musical.ticket.repository.VenueRepository;
import com.musical.ticket.util.FileUtil; // 👈 [추가!]
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final FileUtil fileUtil; // 👈 [추가!]

    //(Admin) 공연장 등록(C)
    @Transactional
    public VenueResDto saveVenue(VenueSaveReqDto reqDto, MultipartFile layoutImage){ // 👈 [수정!]
        // 1. (신규) 좌석 배치도 이미지 저장
        String layoutImageUrl = fileUtil.saveFile(layoutImage);

        // 2. 공연장(Venue) 정보 저장 (이미지 URL 포함)
        Venue venue = reqDto.toEntity(layoutImageUrl); // 👈 [수정!]
        Venue savedVenue = venueRepository.save(venue);

        // 3. 좌석 템플릿(Seat) DTO -> Entity 변환 및 저장
        List<Seat> seats = reqDto.getSeats().stream()
                .map(seatDto -> seatDto.toEntity(savedVenue))
                .collect(Collectors.toList());
        seatRepository.saveAll(seats);

        // 4. 저장된 Venue 반환 (N+1 방지)
        return new VenueResDto(savedVenue, seats);
    }

    //(Admin) 공연장 수정(U)
    @Transactional
    public VenueResDto updateVenue(Long venueId, VenueSaveReqDto reqDto, MultipartFile layoutImage) {
        
        Venue venue = venueRepository.findByIdWithFetch(venueId) // 👈 [수정!]
                 .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        // 1. (신규) 새 배치도 이미지 처리
        String newLayoutImageUrl = venue.getLayoutImageUrl(); // (기존 URL 유지)
        if (layoutImage != null && !layoutImage.isEmpty()) {
            fileUtil.deleteFile(venue.getLayoutImageUrl()); // (기존 이미지 삭제)
            newLayoutImageUrl = fileUtil.saveFile(layoutImage); // (새 이미지 저장)
        }
        
        // 2. 공연장 정보 업데이트 (Dirty Checking)
        venue.update(reqDto.getName(), reqDto.getLocation(), newLayoutImageUrl, reqDto.getRegion()); // 👈 [수정!]
        
        // 3. (중요) 기존 좌석 템플릿은 모두 삭제
        seatRepository.deleteAll(venue.getSeats());
        venue.getSeats().clear();

        // 4. 새 좌석 템플릿 리스트 생성 및 저장
        List<Seat> newSeats = reqDto.getSeats().stream()
                .map(seatDto -> seatDto.toEntity(venue))
                .collect(Collectors.toList());
        seatRepository.saveAll(newSeats);

        // 5. 업데이트된 정보 반환
        return new VenueResDto(venue, newSeats);
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