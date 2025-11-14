package com.musical.ticket.dto.venue;

import java.util.List;
import com.musical.ticket.domain.entity.Venue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VenueSaveReqDto {

    @NotBlank
    private String name;

    private String location;

    @NotEmpty(message = "좌석의 탬플릿은 최소 1개 이상 등록해야 합니다.")
    @Valid
    private List<SeatTemplateReqDto> seats;

    // 👇 추가: 배경 이미지 URL
    private String layoutImageUrl;

    // Service에서 Entity로 변환(좌석 제외, 공연장 정보만)
    public Venue toEntity(){
        return Venue.builder()
            .name(this.name)
            .location(this.location)
            .layoutImageUrl(this.layoutImageUrl)  // 👈 추가
            .build();
    }
}