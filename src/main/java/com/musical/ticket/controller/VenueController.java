package com.musical.ticket.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.musical.ticket.dto.venue.VenueResDto;
import com.musical.ticket.dto.venue.VenueSaveReqDto;
import com.musical.ticket.service.VenueService;

import jakarta.validation.Valid; // Valid import 확인
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
    
    private final VenueService venueService;

    // 공연장 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResDto> saveVenue(
        @Valid @RequestPart("venueDto") VenueSaveReqDto reqDto, // "venue" -> "venueDto" 수정
        @RequestPart(value = "layoutImage", required = false) MultipartFile layoutImage
    ){
        VenueResDto responseDto = venueService.saveVenue(reqDto, layoutImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 공연장 목록 조회
    @GetMapping
    public ResponseEntity<List<VenueResDto>> getAllVenues(){
        return ResponseEntity.ok(venueService.getAllVenues());
    }
}