package com.musical.ticket.dto.performance;
// 공연 회차 등록, 수정 요청 dto
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import com.musical.ticket.domain.entity.Musical;
import com.musical.ticket.domain.entity.Performance;

@Getter
@Setter
@NoArgsConstructor
public class PerformanceSaveReqDto {

    @NotNull
    private Long musicalId; // 어떤 뮤지컬의 회차인지

    @NotNull
    @Future(message = "공연 시간은 현재 시간 이후여야 합니다.") // 미래 시간만 가능
    private LocalDateTime performanceDate;

    @NotBlank
    private String venue; // 공연 장소

    @Builder
    public PerformanceSaveReqDto(Long musicalId, LocalDateTime performanceDate, String venue) {
        this.musicalId = musicalId;
        this.performanceDate = performanceDate;
        this.venue = venue;
    }

    // DTO를 Entity로 변환 (Musical 객체는 Service에서 조회 후 주입)
    public Performance toEntity(Musical musical) {
        return Performance.builder()
                .musical(musical)
                .performanceDate(this.performanceDate)
                .venue(this.venue)
                .build();
    }
}
