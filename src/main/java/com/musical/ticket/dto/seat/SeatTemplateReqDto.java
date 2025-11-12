package com.musical.ticket.dto.seat;
//관리자가 공연장을 등록할 때, 좌석 정보(예 : "vip석"에 "a-10열")를 함께 보내야 함
//이 좌석 탬플릿 하나하나를 객체로 받기 위한 용도

import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.domain.enums.SeatGrade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeatTemplateReqDto {

    @NotBlank
    private String seatNumber; // 예: "1층 A열 10번"

    @NotNull
    private SeatGrade seatGrade; //dP : VIP

    @NotNull
    private Integer xCoord;

    @NotNull
    private Integer yCoord;

    //Service에서 Venue객체를 주입받아 Entity로 변환
    public Seat toEntity(Venue venue){
        return Seat.builder()
            .venue(venue) // 연관관계(N:1) 설정
            .seatGrade(this.seatGrade)
            .seatNumber(this.seatNumber)
            .xCoord(this.xCoord)
            .yCoord(this.yCoord)
            .build();
    }
    
}
