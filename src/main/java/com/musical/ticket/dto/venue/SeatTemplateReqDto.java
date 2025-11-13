package com.musical.ticket.dto.venue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musical.ticket.domain.entity.Seat;
import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.domain.enums.SeatGrade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// (Lombok 임포트 모두 제거)

// [수정!] @Getter, @Setter, @NoArgsConstructor 어노테이션 제거
public class SeatTemplateReqDto {

    @NotBlank(message = "seatNumber는 널이어서는 안됩니다")
    private String seatNumber;

    @NotNull(message = "seatGrade는 널이어서는 안됩니다")
    private SeatGrade seatGrade;

    @JsonProperty("xCoord")
    @NotNull(message = "xCoord는 널이어서는 안됩니다")
    private Integer xCoord;

    @JsonProperty("yCoord")
    @NotNull(message = "yCoord는 널이어서는 안됩니다")
    private Integer yCoord;

    // --- 👇 [신규!] Lombok이 하던 일을 "수동"으로 추가 ---
    
    // 1. 기본 생성자 (Jackson이 객체 생성 시 필요)
    public SeatTemplateReqDto() {
    }

    // 2. Getter (toEntity에서 사용)
    public String getSeatNumber() { return seatNumber; }
    public SeatGrade getSeatGrade() { return seatGrade; }
    public Integer getXCoord() { return xCoord; }
    public Integer getYCoord() { return yCoord; }

    // 3. Setter (Jackson이 JSON을 Java로 변환 시 필요)
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public void setSeatGrade(SeatGrade seatGrade) { this.seatGrade = seatGrade; }
    public void setXCoord(Integer xCoord) { this.xCoord = xCoord; }
    public void setYCoord(Integer yCoord) { this.yCoord = yCoord; }
    // --- 👆 [신규!] ---


    // (toEntity 메서드는 동일)
    public Seat toEntity(Venue venue) {
        return Seat.builder()
                .venue(venue) 
                .seatGrade(this.seatGrade)
                .seatNumber(this.seatNumber)
                
                // --- 👇👇👇 [핵심 수정!] DTO의 'xCoordinate'를 Entity의 'xCoord'로 전달 ---
                .xCoord(this.xCoord) 
                .yCoord(this.yCoord)
                // --- 👆👆👆 ---
                
                .build();
    }
}