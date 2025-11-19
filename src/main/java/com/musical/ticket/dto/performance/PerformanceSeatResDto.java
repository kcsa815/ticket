package com.musical.ticket.dto.performance;
/**
 * 작성자 : suan
 * 
 * PerformanceResDto 내부에 포함되는 dto
 * 예매 가능한 개별 좌석의 상태(좌석번호, 등급, 가격, 예약여부)를 담고있음
 * 응답 전용이기 때문에 Setter, NoArgsConstructor 필요 없음
 * 
 * 수정일 : 2025-11-14 (좌석배치도 x,y좌표 추가)
 */

import com.musical.ticket.domain.entity.PerformanceSeat;
import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.enums.SeatGrade;
import lombok.Getter;

@Getter
public class PerformanceSeatResDto {

    private Long performanceSeatId;
    private String seatNumber;
    private SeatGrade seatGrade;
    private Integer price;
    private Boolean isReserved;
    
    // --- 👇 [핵심!] 좌표 필드 추가 ---
    private Integer xCoord;
    private Integer yCoord;
    // --- 👆 ---

    public PerformanceSeatResDto(PerformanceSeat performanceSeat) {
        this.performanceSeatId = performanceSeat.getId();
        this.price = performanceSeat.getPrice();
        this.isReserved = performanceSeat.getIsReserved();
        
        // Seat 템플릿에서 정보 가져오기
        Seat seat = performanceSeat.getSeat();
        this.seatNumber = seat.getSeatNumber();
        this.seatGrade = seat.getSeatGrade();
        
        // --- 👇 [핵심!] 좌표 값 꺼내오기 ---
        this.xCoord = seat.getXCoord();
        this.yCoord = seat.getYCoord();
        // --- 👆 ---
    }
}