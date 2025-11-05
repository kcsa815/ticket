package com.musical.ticket.dto.performance;
// 공연 회차 상세정보 응답용 dto
// 공연 기본정보, 이 회차의 모든 좌석 맵(List<PerformanceSeatResDto>포함)
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.musical.ticket.domain.entity.Performance;

@Getter
public class PerformanceResDto {
    private Long performanceId;
    private String musicalTitle; 
    private String venueName;
    private LocalDateTime performanceDate;
    private List<PerformanceSeatResDto> seats; //이 공연의 전체 좌석 맵
    

    public PerformanceResDto(Performance performance) {
        this.performanceId = performance.getId();
        this.musicalTitle = performance.getMusical().getTitle(); // Lazy 로딩 주의
        this.venueName = performance.getVenue().getName();         // Lazy 로딩 주의
        this.performanceDate = performance.getPerformanceDate();
        
        //Performance 엔티티가 가진 List<PerformanceSeat>를 List<PerformanceSeatResDto>로 변환
        this.seats = performance.getPerformanceSeats().stream()
            .map(PerformanceSeatResDto::new)
            .collect(Collectors.toList());
    }
}
