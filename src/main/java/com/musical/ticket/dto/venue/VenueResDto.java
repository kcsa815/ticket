package com.musical.ticket.dto.venue;
/**
 * 작성자 : suan
 * 
 * 공연장 정보와 그 공연장에 속한 좌석 탬플릿 목록(List<SeatResDto>)을 함께 반환하여
 * 클라이언트가 응답을 받자마자 구조 전체를 파악할 수 있게 하는 dto
 * 
 * !!!!공연등록이 성공했을 때, 서버가 클라이언트에게 "등록 완료!" 라고 보내주는 응답서
 * VenueResDto.java(응답서) 가 SeatResDto.java(응답용 좌석 블록)를 포함하는 구조
 * 
 * 최종 수정일 : 2025-11-13
 * */ 
import java.util.List;
import java.util.stream.Collectors;

import com.musical.ticket.domain.entity.Venue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
