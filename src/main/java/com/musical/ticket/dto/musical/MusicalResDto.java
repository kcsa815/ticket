package com.musical.ticket.dto.musical;
//뮤지컬 정보 응답 -목록, 상세 공유 dto
import com.musical.ticket.domain.entity.Musical;
import lombok.Getter;

@Getter
public class MusicalResDto {
    private Long musicalId;
    private String title;
    private String description;
    private String posterImageUrl;
    private Integer runningTime;
    private String ageRating;

    public MusicalResDto(Musical musical) {
        this.musicalId = musical.getId();
        this.title = musical.getTitle();
        this.description = musical.getDescription();
        this.posterImageUrl = musical.getPosterImageUrl();
        this.runningTime = musical.getRunningTime();
        this.ageRating = musical.getAgeRating();
    }
}
