package com.musical.ticket.dto.musical;
import org.springframework.web.multipart.MultipartFile;
/* 
 * 작성자 : suan
 * 뮤지컬 등록, 수정 요청 dto
 * 
 * 수정일 : 2025-11-13
*/

import com.musical.ticket.domain.entity.Musical;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    
    private MultipartFile posterImage;

    private String category;

    @NotNull
    @Min(value = 1, message = "상영 시간은 1분 이상이어야 합니다.")
    private Integer runningTime;

    private String ageRating;

    public Musical toEntity(String posterImageUrl) {
        return Musical.builder()
                .title(this.title)
                .description(this.description)
                .posterImageUrl(posterImageUrl) //service에서 저장한 url
                .runningTime(this.runningTime)
                .ageRating(this.ageRating)
                .category(this.category)
                .build();
    }
}