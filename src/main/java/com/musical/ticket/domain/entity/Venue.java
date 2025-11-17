package com.musical.ticket.domain.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "venue")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    // (신규) 좌석 배치도 이미지 URL
    @Column(length = 500)
    private String layoutImageUrl;

    @Column(length = 50, nullable = false)
    private String region;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "venue")
    private List<Performance> performances = new ArrayList<>();

    // (신규) Builder (layoutImageUrl, region 포함)
    @Builder
    public Venue(String name, String location, String layoutImageUrl, String region) {
        this.name = name;
        this.location = location;
        this.layoutImageUrl = layoutImageUrl;
        this.region = (region !=null) ? region : "DEFAULT";
    }

    // (신규) Service가 호출할 update 메서드
    public void update(String name, String location, String layoutImageUrl, String region) {
        this.name = name;
        this.location = location;
        this.layoutImageUrl = layoutImageUrl;
        this.region = (region != null) ? region : "DEFAULT";
    }
}