package com.musical.ticket.dto.musical;
//뮤지컬 등록, 수정 요청 dto

import com.musical.ticket.domain.entity.Musical;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MusicalSaveReqDto {

    @NotBlank
    private String title;

    private String description;
    
    private String posterImageUrl;

    @NotNull
    @Min(value = 1, message = "상영 시간은 1분 이상이어야 합니다.")
    private Integer runningTime;

    private String ageRating;

    @Builder
    public MusicalSaveReqDto(String title, String description, String posterImageUrl, Integer runningTime, String ageRating) {
        this.title = title;
        this.description = description;
        this.posterImageUrl = posterImageUrl;
        this.runningTime = runningTime;
        this.ageRating = ageRating;
    }

    public Musical toEntity() {
        return Musical.builder()
                .title(this.title)
                .description(this.description)
                .posterImageUrl(this.posterImageUrl)
                .runningTime(this.runningTime)
                .ageRating(this.ageRating)
                .build();
    }
}