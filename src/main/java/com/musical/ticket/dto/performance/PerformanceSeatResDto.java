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
    
    private Long performanceSeatId;     //예매할 때 사용할 ID
    private Long seatId;                //좌석 탬플릿 원본 ID
    private String seatNumber;          //좌석 번호 ("예 : "A-10")
    private SeatGrade seatGrade;        //좌석 등급 ("예 : "VIP")
    private Integer price;              // 이 회차의 가격
    private Boolean isReserved;         //예약 상태
    private Integer xCoord;             //x좌표 필드
    private Integer yCoord;             //y좌표 필드

    public PerformanceSeatResDto(PerformanceSeat performanceSeat){
        Seat seat = performanceSeat.getSeat();

        this.performanceSeatId = performanceSeat.getId();
        this.price = performanceSeat.getPrice();
        this.isReserved = performanceSeat.getIsReserved();

        //Seat 템플릿에서 정보 가져오기
        this.seatNumber = seat.getSeatNumber();
        this.seatGrade = seat.getSeatGrade();
        
        //좌표 값 할당
        this.xCoord = seat.getXCoord(); 
        this.yCoord = seat.getYCoord();
    }
}
