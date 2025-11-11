package com.musical.ticket.controller;
//@PreAuthorize("hasRole('ADMIN')") : 공연 등록은 ADMIN만 할 수 있어야 함.
//@Valid @RequestBody : dto의 Map과 @Future 날짜 등 유효성 검사를 통과한 JSON데이터만 PerformanceSaveReqDto객체로 받음

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.musical.ticket.dto.performance.PerformanceResDto;
import com.musical.ticket.dto.performance.PerformanceSaveReqDto;
import com.musical.ticket.dto.performance.PerformanceSimpleResDto;
import com.musical.ticket.service.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {
    
    private final PerformanceService performanceService;

    //(Admin) 공연 회차 등록(C) - 좌석 자동 생성 포함
    //[POST] /api/performances
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> savePerformance(@Valid @RequestBody PerformanceSaveReqDto reqDto){
        Long performanceId = performanceService.savePerformance(reqDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceId);
    }

    //(All) 특정 뮤지컬의 모든 공연 회차 목록 조회 (R)
    //[GET] /api/performances/musical/{musicalId}
    @GetMapping("/musical/{musicalId}") 
    public ResponseEntity<List<PerformanceSimpleResDto>> getPerformancesByMusical(@PathVariable Long musicalId) {
        List<PerformanceSimpleResDto> responseDtos = performanceService.getPerformancesByMusical(musicalId);
        return ResponseEntity.ok(responseDtos);
    }

    //(All) 공연 회차 상세 조회(좌석 맵 포함)(R)
    //[GET] /api/performances/{performanceId}
    @GetMapping("/{performanceId}")
    public ResponseEntity<PerformanceResDto> getPerformanceDtails(@PathVariable Long performanceId) {
        PerformanceResDto responseDto = performanceService.getPerformanceDetails(performanceId);
        return ResponseEntity.ok(responseDto);
    }
    
}    


