package com.musical.ticket.dto.musical;

import com.musical.ticket.domain.entity.Musical;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MusicalSaveReqDto {

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    private String title;

    private String description;
    
    private String category;

    @NotNull(message = "관람 시간은 널이어서는 안됩니다.")
    private Integer runningTime; 

    private String ageRating;

    // [수동 toEntity] - Entity의 수동 생성자를 호출
    public Musical toEntity(String posterImageUrl) {
        return new Musical(
                this.title,
                this.description,
                posterImageUrl, 
                this.runningTime,
                this.ageRating,
                this.category
        );
    }
}