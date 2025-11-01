package com.musical.ticket.dto.performance;
//공연 회차 응답
import lombok.Getter;
import java.time.LocalDateTime;
import com.musical.ticket.domain.entity.Performance;

@Getter
public class PerformanceResDto {
    private Long performanceId;
    private Long musicalId;
    private String musicalTitle; // Join해서 가져오면 좋음 (나중에 Querydsl 등으로 최적화)
    private LocalDateTime performanceDate;
    private String venue;

    public PerformanceResDto(Performance performance) {
        this.performanceId = performance.getId();
        this.musicalId = performance.getMusical().getId();
        this.musicalTitle = performance.getMusical().getTitle(); // Lazy 로딩 주의
        this.performanceDate = performance.getPerformanceDate();
        this.venue = performance.getVenue();
    }
}
