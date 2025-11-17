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

    @NotBlank(message = "region은 널이어서는 안됩니다")
    private String region; // (예: "SEOUL")

    @NotEmpty(message = "좌석 템플릿은 최소 1개 이상 등록해야 합니다.")
    @Valid 
    private List<SeatTemplateReqDto> seats;

    // [수정!] Service에서 layoutImageUrl을 받아 Entity로 변환
    public Venue toEntity(String layoutImageUrl){
        return Venue.builder()
                .name(this.name)
                .location(this.location)
                .region(this.region)
                .layoutImageUrl(layoutImageUrl) // 👈 [수정!]
                .build();
    }
}