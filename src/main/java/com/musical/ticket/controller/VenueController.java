package com.musical.ticket.controller;
//클라이언트의 http 요청을 가장 먼저 받는 관문
    //@RestController : 이 클래스가 JSON을 반환하는 API컨트롤러임을 선언함
    //@RequestMapping("/api/venues") : 이 컨트롤러의 모든 API는 /api/venues라는 기본 URL을 가짐.
    //@PostMapping : http POST요청을 처리함(Create)
    //@RequestBody : 클리이언트가 보낸 JSON데이터를 VenueSaveReqDto자바 객체로 자동 변환해줌
    //@Valid : @RequestBody로 변환된 VenueSaveReqDto 객체에 대해, dto에 설정한 유효성 검사(@NotBlank, NotEmpty)를 실행함
    // 만약 실패하면 GlobalExceptionHandler가 MethodArgumentNotValidException을 잡아채 400에러를 반환함
    //@PreAuthorize("hasRole('ADMIN')") : Spring Security의 메서드 레벨 보안.
        //이 API를 실행하기 전, 현재 로그인한 사용자의 권한을 확인함.
        //ROLE_ADMIN 권한을 가진 사용자(JWT토큰기준)만 이 메서드를 통과할 수 있음.
        //ROLE_USER나 비로그인 사용자가 호출하면 CustomAccessDeniedHandler가 403 Forbidden을 반환함.

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.musical.ticket.dto.venue.VenueResDto;
import com.musical.ticket.dto.venue.VenueSaveReqDto;
import com.musical.ticket.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
    
    private final VenueService venueService;

    //(Admin) 공연장 및 좌석 템플릿 등록(C)
    // [GET] /api/venues
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") //ADMIN만이 이 API호출 가능
    public ResponseEntity<VenueResDto> saveVenue(@Valid @RequestBody VenueSaveReqDto reqDto){
        VenueResDto responseDto = venueService.saveVenue(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    //(All) 공연장 전체 목록 조회(R)
    // [POST] /api/venues
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
