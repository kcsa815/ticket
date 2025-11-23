package com.musical.ticket.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

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

    //(Admin) 뮤지컬 등록(C) - (동일)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MusicalResDto> saveMusical(
        @Valid @RequestPart("musicalDto") MusicalSaveReqDto reqDto,
        @RequestPart(value = "posterImage", required = false) MultipartFile posterImage 
    ){
        MusicalResDto responseDto = musicalService.saveMusical(reqDto, posterImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    //(User/All)뮤지컬 전체 목록 조회(R) - (동일)
    @GetMapping
    public ResponseEntity<List<MusicalResDto>> getAllMusicals(
            @RequestParam(name = "section", required = false) String section
    ) {
        List<MusicalResDto> responseDtos = musicalService.getAllMusicals(section);
        return ResponseEntity.ok(responseDtos);
    }

    //(User/All) 뮤지컬 상세 조회(R) - (동일)
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
        // 👇 [Fix 1: @RequestPart로 DTO 수신]
        @Valid @RequestPart("musicalDto") MusicalSaveReqDto reqDto,
        // 👇 [Fix 2: MultipartFile 파라미터 추가]
        @RequestPart(value = "posterImage", required = false) MultipartFile posterImage
    ){
        // 👇 [Fix 3: Service 호출 시 posterImage를 정상적으로 전달]
        MusicalResDto respondResDto = musicalService.updateMusical(musicalId, reqDto, posterImage);
        return ResponseEntity.ok(respondResDto);
    }
    
    //(Admin)뮤지컬 삭제(D) - (동일)
    @DeleteMapping("/{musicalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMusical(@PathVariable Long musicalId){
        musicalService.deleteMusical(musicalId);
        return ResponseEntity.noContent().build();
    }
}