package com.musical.ticket.dto.performance;
// PerformanceResDto 내부에 포함되는 dto
// 예매 가능한 개별 좌석의 상태(좌석번호, 등급, 가격, 예약여부)를 담고있음

import com.musical.ticket.domain.entity.PerformanceSeat;
import com.musical.ticket.domain.enums.SeatGrade;
import lombok.Getter;

@Getter
public class PerformanceSeatResDto {
    
    private Long performanceSeatId; //예매할 때 사용할 ID
    private Long seatId; //좌석 탬플릿 원본 ID
    private String seatNumber; //좌석 번호 ("예 : "A-10")
    private SeatGrade seatGrade; //좌석 등급 ("예 : "VIP")
    private Integer price; // 이 회차의 가격
    private Boolean isReserved; //예약 상태

    public PerformanceSeatResDto(PerformanceSeat performanceSeat){
        this.performanceSeatId = performanceSeat.getId();
        this.seatId = performanceSeat.getSeat().getId();
        this.seatNumber = performanceSeat.getSeat().getSeatNumber();
        this.seatGrade = performanceSeat.getSeat().getSeatGrade();
        this.price = performanceSeat.getPrice();
        this.isReserved = performanceSeat.getIsReserved();
    }
}
