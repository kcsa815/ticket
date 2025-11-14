package com.musical.ticket.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.musical.ticket.dto.venue.VenueResDto;
import com.musical.ticket.dto.venue.VenueSaveReqDto;
import com.musical.ticket.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
    
    private final VenueService venueService;

    //(Admin) 공연장 및 좌석 템플릿 등록(C)
    // [POST] /api/venues
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResDto> saveVenue(
        @Valid @RequestPart("venue") VenueSaveReqDto reqDto,
        @RequestPart(value = "layoutImage", required = false) MultipartFile layoutImage
    ){
        VenueResDto responseDto = venueService.saveVenue(reqDto, layoutImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    //(Admin) 공연장 수정(U)
    // [PUT] /api/venues/{venueId}
    @PutMapping(value = "/{venueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  // 👈 이 부분!
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResDto> updateVenue(
        @PathVariable Long venueId,
        @Valid @RequestPart("venue") VenueSaveReqDto reqDto,
        @RequestPart(value = "layoutImage", required = false) MultipartFile layoutImage
    ){
        VenueResDto responseDto = venueService.updateVenue(venueId, reqDto, layoutImage);
        return ResponseEntity.ok(responseDto);
    }

    //(All) 공연장 전체 목록 조회(R)
    // [GET] /api/venues
    @GetMapping
    public ResponseEntity<List<VenueResDto>> getAllVenues(){
        List<VenueResDto> responseDtos = venueService.getAllVenues();
        return ResponseEntity.ok(responseDtos);
    }

    //(All) 공연장 상세 조회(R)
    // [GET] /api/venues/{venueId}
    @GetMapping("/{venueId}")
    public ResponseEntity<VenueResDto> getVenueById(@PathVariable Long venueId){
        VenueResDto responseDto = venueService.getVenueById(venueId);
        return ResponseEntity.ok(responseDto);
    }
}