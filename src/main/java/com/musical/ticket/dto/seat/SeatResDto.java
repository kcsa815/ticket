package com.musical.ticket.dto.seat;
//좌석 정보 응답 dto

import com.musical.ticket.domain.entity.PerformanceSeat;
import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.enums.SeatGrade;
import lombok.Getter;

@Getter
public class SeatResDto {
    private Long seatId;
    private Long venueId;
    private SeatGrade seatGrade;
    private String seatNumber;
    private Integer price;

    // VenueSaveReqDto를 받는 생성자 (price, isReserved 정보 포함)
    public SeatResDto(PerformanceSeat performanceSeat) {
        Seat seat = performanceSeat.getSeat();
        this.seatId = seat.getId();
        this.venueId = seat.getVenue().getId();
        this.seatGrade = seat.getSeatGrade();
        this.seatNumber = seat.getSeatNumber();
        this.price = performanceSeat.getPrice();
    }

    // Seat만 받는 생성자 (price, isReserved는 null로 설정)
    public SeatResDto(Seat seat) {
        this.seatId = seat.getId();
        this.venueId = seat.getVenue().getId();
        this.seatGrade = seat.getSeatGrade();
        this.seatNumber = seat.getSeatNumber();
        this.price = null;
    }
}