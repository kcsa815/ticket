package com.musical.ticket.dto.musical;
//뮤지컬 정보 응답 -목록, 상세 공유 dto
import com.musical.ticket.domain.entity.Musical;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MusicalResDto {
    private Long musicalId;
    private String title;
    private String description;
    private String posterImageUrl;
    private Integer runningTime;
    private String ageRating;
    private Integer minPrice;
    private Integer maxPrice;
    private String category;

    @Setter
    private String venueName;

    /*기존 생성자 */
    public MusicalResDto(Musical musical) {
        this.musicalId = musical.getId();
        this.title = musical.getTitle();
        this.description = musical.getDescription();
        this.posterImageUrl = musical.getPosterImageUrl();
        this.runningTime = musical.getRunningTime();
        this.ageRating = musical.getAgeRating();
        this.minPrice = null;
        this.maxPrice = null;
        this.category = musical.getCategory();
        this.venueName = null;
    }

    /*가격 포함 생성자 */
    public MusicalResDto(Musical musical, Integer minPrice, Integer maxPrice){
        this(musical);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
