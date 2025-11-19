package com.musical.ticket.dto.performance;

import com.musical.ticket.domain.entity.Performance;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PerformanceSimpleResDto {

    private Long performanceId;
    private LocalDateTime performanceDate;
    private String venueName;
    private String musicalTitle;
 
    // --- 상세 페이지 이동 & 포스터용 필드 추가 ---
    private Long musicalId;
    private String posterImageUrl;

    public PerformanceSimpleResDto(Performance performance) {
        this.performanceId = performance.getId();
        this.performanceDate = performance.getPerformanceDate();
        this.venueName = performance.getVenue().getName();
        this.musicalTitle = performance.getMusical().getTitle();
        this.musicalId = performance.getMusical().getId();
        this.posterImageUrl = performance.getMusical().getPosterImageUrl();
    }
}