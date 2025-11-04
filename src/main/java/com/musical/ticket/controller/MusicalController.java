package com.musical.ticket.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.musical.ticket.dto.musical.MusicalResDto;
import com.musical.ticket.dto.musical.MusicalSaveReqDto;
import com.musical.ticket.service.MusicalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/musicals")
@RequiredArgsConstructor
public class MusicalController {

    private final MusicalService musicalService;

    //(Admin) 뮤지컬 등록(C)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MusicalResDto> saveMusical(
        @Valid @ModelAttribute MusicalSaveReqDto reqDto
    ) {
        MusicalResDto responseDto = musicalService.saveMusical(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    //(User/All)뮤지컬 전체 목록 조회(R)
    @GetMapping
    public ResponseEntity<List<MusicalResDto>> getAllMusicals(){
        List<MusicalResDto> responseDtos = musicalService.getAllMusicals();
        return ResponseEntity.ok(responseDtos);
    };

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
        @Valid @ModelAttribute MusicalSaveReqDto reqDto
    ){
        MusicalResDto respondResDto = musicalService.updateMusical(musicalId, reqDto);
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
