package com.musical.ticket.dto.venue;

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

    @NotBlank(message = "seatNumber는 널이어서는 안됩니다")
    private String seatNumber;

    @NotNull(message = "seatGrade는 널이어서는 안됩니다")
    private SeatGrade seatGrade;

    @NotNull(message = "xCoord는 널이어서는 안됩니다")
    private Integer xCoord;

    @NotNull(message = "yCoord는 널이어서는 안됩니다")
    private Integer yCoord;

    // Service에서 Venue 객체를 주입받아 Entity로 변환
    public Seat toEntity(Venue venue) {
        return Seat.builder()
                .venue(venue) 
                .seatGrade(this.seatGrade)
                .seatNumber(this.seatNumber)
                .xCoord(this.xCoord) 
                .yCoord(this.yCoord) 
                .build();
    }
}