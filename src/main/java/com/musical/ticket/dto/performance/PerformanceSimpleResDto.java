package com.musical.ticket.dto.performance;
// 공연 회차 목록 응답용 dto
// 뮤지컬 상세 페이지에서 "회차 목록 보기"를 누르면, 각 회차의 간단한 정보들만 리스트로 보여줄 수 있음
// PerformanceResDto를 리스트로 반환하면 좌석 맵 데이터가 중복되어 무겁기때문에 별도로 설계한 dto

import java.time.LocalDateTime;

import com.musical.ticket.domain.entity.Performance;

import lombok.Getter;

@Getter
public class PerformanceSimpleResDto {
    
    private Long performanceId;
    private Long musicalId;
    private String musicalTitle;
    private String venueName;
    private LocalDateTime performanceDate;
    private String posterImageUrl; //목록이라서 포스터도 같이 전달

    public PerformanceSimpleResDto(Performance performance){
        this.performanceId = performance.getId();
        this.musicalId = performance.getMusical().getId();
        this.musicalTitle = performance.getMusical().getTitle();
        this.venueName = performance.getVenue().getName();
        this.performanceDate = performance.getPerformanceDate();
        this.posterImageUrl = performance.getMusical().getPosterImageUrl();
    }
}
