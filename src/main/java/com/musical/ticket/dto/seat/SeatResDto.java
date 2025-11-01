package com.musical.ticket.dto.seat;
//좌석 정보 응답 dto

import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.enums.SeatGrade;
import lombok.Getter;

@Getter
public class SeatResDto {
    private Long seatId;
    private Long performanceId;
    private SeatGrade seatGrade;
    private String seatNumber;
    private Integer price;
    private Boolean isReserved;

    public SeatResDto(Seat seat) {
        this.seatId = seat.getId();
        this.performanceId = seat.getPerformance().getId();
        this.seatGrade = seat.getSeatGrade();
        this.seatNumber = seat.getSeatNumber();
        this.price = seat.getPrice();
        this.isReserved = seat.getIsReserved();
    }
}