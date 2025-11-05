package com.musical.ticket.dto.booking;
//예매 요청 dto

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookingReqDto {

    @NotEmpty(message = "좌석을 하나 이상 선택해야 합니다.")
    private List<Long> performanceSeatIds; // 예매할 '공연 좌석'의 고유 ID목록

}