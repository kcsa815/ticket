package com.musical.ticket.dto.booking;
//예매 요청 dto

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookingReqDto {

    @NotNull
    private Long performanceId;

    @NotEmpty(message = "좌석을 하나 이상 선택해야 합니다.")
    private List<Long> seatIds; // 예매할 좌석 ID 목록

    @Builder
    public BookingReqDto(Long performanceId, List<Long> seatIds) {
        this.performanceId = performanceId;
        this.seatIds = seatIds;
    }
}