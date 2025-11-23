package com.musical.ticket.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.musical.ticket.dto.musical.MusicalResDto;
import com.musical.ticket.dto.musical.MusicalSaveReqDto;
import com.musical.ticket.service.MusicalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/musicals")
@RequiredArgsConstructor
public class MusicalController {

    private final MusicalService musicalService;

    //(Admin) 뮤지컬 등록(C)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MusicalResDto> saveMusical(
        @Valid @RequestPart("musicalDto") MusicalSaveReqDto reqDto, 
        @RequestPart(value = "posterImage", required = false) MultipartFile posterImage 
    ){
        MusicalResDto responseDto = musicalService.saveMusical(reqDto, posterImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    //(User/All)뮤지컬 전체 목록 조회(R)
    @GetMapping
    public ResponseEntity<List<MusicalResDto>> getAllMusicals(
            @RequestParam(name = "section", required = false) String section
    ) {
        List<MusicalResDto> responseDtos = musicalService.getAllMusicals(section);
        return ResponseEntity.ok(responseDtos);
    }

    //(User/All) 뮤지컬 상세 조회(R)
    @GetMapping("/{musicalId}")
    public ResponseEntity<MusicalResDto> getMusicalById(@PathVariable Long musicalId){
        MusicalResDto responseDto = musicalService.getMusicalById(musicalId);
        return ResponseEntity.ok(responseDto);
    }

    //(Admin) 뮤지컬 정보 수정(U)
    @PutMapping(value = "/{musicalId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MusicalResDto> updateMusical(
        @PathVariable Long musicalId,
        //  @RequestPart로 JSON Blob을 받음
        @Valid @RequestPart("musicalDto") MusicalSaveReqDto reqDto,
        @RequestPart(value = "posterImage", required = false) MultipartFile posterImage
    ){
        MusicalResDto respondResDto = musicalService.updateMusical(musicalId, reqDto, posterImage);
        return ResponseEntity.ok(respondResDto);
    }
    
    //(Admin)뮤지컬 삭제(D)
    @DeleteMapping("/{musicalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMusical(@PathVariable Long musicalId){
        musicalService.deleteMusical(musicalId);
        return ResponseEntity.noContent().build();
    }
}