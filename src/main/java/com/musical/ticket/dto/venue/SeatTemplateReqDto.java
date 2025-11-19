package com.musical.ticket.dto.venue;

import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.domain.enums.SeatGrade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// (Lombok 제거 - 수동 Getter/Setter)
public class SeatTemplateReqDto {

    @NotBlank(message = "seatNumber는 널이어서는 안됩니다")
    private String seatNumber;

    @NotNull(message = "seatGrade는 널이어서는 안됩니다")
    private SeatGrade seatGrade;

    @NotNull(message = "xCoord는 널이어서는 안됩니다")
    private Integer xCoord;

    @NotNull(message = "yCoord는 널이어서는 안됩니다")
    private Integer yCoord;

    // (기본 생성자)
    public SeatTemplateReqDto() {}

    // (Getter)
    public String getSeatNumber() { return seatNumber; }
    public SeatGrade getSeatGrade() { return seatGrade; }
    public Integer getXCoord() { return xCoord; }
    public Integer getYCoord() { return yCoord; }

    // (Setter)
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public void setSeatGrade(SeatGrade seatGrade) { this.seatGrade = seatGrade; }
    public void setXCoord(Integer xCoord) { this.xCoord = xCoord; }
    public void setYCoord(Integer yCoord) { this.yCoord = yCoord; }

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