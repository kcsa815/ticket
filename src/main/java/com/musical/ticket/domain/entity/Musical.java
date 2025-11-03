package com.musical.ticket.domain.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "musical")
public class Musical {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "musical_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob //text타입 매핑
    private String description;

    @Column(name = "poster_image_url")
    private String posterImageUrl;

    @Column(name = "running_time", nullable = false)
    private Integer runningTime;

    @Column(name = "age_rating")
    private String ageRating;

    // 1:M 관계
    @OneToMany(mappedBy = "musical", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Performance> performances = new ArrayList<>();

    @Builder
    public Musical(String title, String description, String posterImageUrl, Integer runningTime, String ageRating) {
        this.title = title;
        this.description = description;
        this.posterImageUrl = posterImageUrl;
        this.runningTime = runningTime;
        this.ageRating = ageRating;
    }

    // 뮤지컬 정보 수정 메서드
    public void update(String title, String description, String posterImageUrl, Integer runningTime, String ageRating) {
        this.title = title;
        this.description = description;
        this.posterImageUrl = posterImageUrl;
        this.runningTime = runningTime;
        this.ageRating = ageRating;
    }

}
