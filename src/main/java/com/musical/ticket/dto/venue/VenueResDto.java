package com.musical.ticket.dto.venue;
//공연장 정보와 그 공연장에 속한 좌석 탬플릿 목록(List<SeatResDto>)을 함께 반환하여
//클라이언트가 응답을 받자마자 구조 전체를 파악할 수 있게 하는 dto

import java.util.List;
import java.util.stream.Collectors;

import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.dto.seat.SeatResDto;

import lombok.Getter;

@Getter
public class VenueResDto {
    private Long venueId;
    private String name;     //공연장 이름(예 : 드림씨어터)
    private String location; //공연장 주소(예 : 부산 남구 전포대로 133)
    private List<SeatResDto> seats; //이 공연장의 좌석 탬플릿 목록

    public VenueResDto(Venue venue){
        this.venueId = venue.getId();
        this.name = venue.getName();
        this.location = venue.getLocation();

        //Entity List -> Dto List 변환
        this.seats = venue.getSeats().stream()
            .map(SeatResDto::new) //seat -> new SeatResDto(seat)
            .collect(Collectors.toList());
    }
}
