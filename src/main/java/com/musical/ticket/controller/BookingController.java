package com.musical.ticket.controller;
/*
 * @PostMapping(CREATE):
     * /api/bookings 경로로 예매 요청을 받음
     * 예매는 로그인 한 사용자만 할 수 있음 : @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
     * 비로그인 사용자 : CustomAuthenticationEntryPoint(401)에 의해 차단됨
     * @Valid @RequestBody : DTO의 List가 @NotEmpty인지 검증
     * 
 * @GetMapping("/my")(Read) : *내 예매 내역*을 조회하는 보너스 API
    * SecurityUtil을 사용하는 Service와 연동하여, 토큰을 기반으로 자신의 예매 내역만 조회함.
 */

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musical.ticket.dto.booking.BookingReqDto;
import com.musical.ticket.dto.booking.BookingResDto;
import com.musical.ticket.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;

    /*
     * (User) 좌석 예매(C)
     * [POST] /api/bookings
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookingResDto> createBooking(@Valid @RequestBody BookingReqDto reqDto){

        BookingResDto responseDto = bookingService.createBooking(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /*
     * (User) 내 예매 내역 조회(R)
     * [GET]
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<BookingResDto>> getMyBookins(){

        List<BookingResDto> responseDtos = bookingService.getMyBookings();
        return ResponseEntity.ok(responseDtos);
    }

    /*
     * (User) 예매 취소(D)
     * [DELETE] /api/bookings/{bookingId}
     */
    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId){

        bookingService.cancelBooking(bookingId);

        //성공시 204 No Content반환
        return ResponseEntity.noContent().build();
    }
}
