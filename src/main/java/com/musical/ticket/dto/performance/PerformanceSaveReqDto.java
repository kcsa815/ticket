package com.musical.ticket.dto.performance;
// 관리자가 공연 회차 등록, 수정 요청 시 보낼 dto
// musicalId, venueId, performanceDate(어떤 공연을, 어떤 공연장에서, 어느 날짜에)
// Map<SeatGrade, Integer>(좌석등급별가격) 정보를 받음(예 : {"VIP" : 170000, "R" : 140000})
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Map;

import com.musical.ticket.domain.entity.Musical;
import com.musical.ticket.domain.entity.Performance;
import com.musical.ticket.domain.entity.Venue;
import com.musical.ticket.domain.enums.SeatGrade;

@Getter
@Setter
@NoArgsConstructor
public class PerformanceSaveReqDto {

    @NotNull
    private Long musicalId; // 어떤 뮤지컬의 회차인지

    @NotNull
    private Long venueId; // 공연 장소 ID

    @NotNull
    @Future(message = "공연 시간은 현재 시간 이후여야 합니다.") // 유효성 검사(미래 시간만 가능) 
    private LocalDateTime performanceDate; //언제 하는지

    @Builder
    public PerformanceSaveReqDto(Long musicalId, LocalDateTime performanceDate, Long venueId) {
        this.musicalId = musicalId;
        this.performanceDate = performanceDate;
        this.venueId = venueId;
    }

    //이 API의 핵심 : "이 공연의 등급별 가격은 얼마인가"
    @NotEmpty(message = "좌석 등급별 가격을 입력해야 합니다.")
    private Map<SeatGrade, Integer> pricesByGrade; // ex : {"VIP" : 170000, "R" : 140000}

    // Service에서 Entity로 변환 (Musical, Venue 객체는 Service에서 조회 후 주입)
    public Performance toEntity(Musical musical, Venue venue) {
        return Performance.builder()
                .musical(musical)
                .venue(venue)
                .performanceDate(this.performanceDate)
                .build();
    }
}
