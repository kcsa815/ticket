package com.musical.ticket.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 1024)
    private String posterImageUrl;

    @Column(name = "running_time", nullable = false)
    private Integer runningTime;

    @Column(name = "age_rating", length = 50)
    private String ageRating;

    @Column(name = "category", length = 50, nullable = false)
    private String category;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // [수동 생성자] Lombok Builder 대신 사용
    public Musical(String title, String description, String posterImageUrl, Integer runningTime, String ageRating, String category) {
        this.title = title;
        this.description = description;
        this.posterImageUrl = posterImageUrl;
        this.runningTime = runningTime;
        this.ageRating = ageRating;
        this.category = (category != null) ? category : "DEFAULT";
    }

    // [수동 Update 메서드]
    public void update(String title, String description, String posterImageUrl, Integer runningTime, String ageRating, String category) {
        this.title = title;
        this.description = description;
        this.posterImageUrl = (posterImageUrl != null) ? posterImageUrl : this.posterImageUrl;
        this.runningTime = (runningTime != null) ? runningTime : this.runningTime;
        this.ageRating = ageRating;
        this.category = (category != null) ? category : "DEFAULT";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}