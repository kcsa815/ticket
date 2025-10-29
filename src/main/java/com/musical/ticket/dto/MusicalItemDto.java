package com.musical.ticket.dto; // 이 클래스는 뮤지컬 데이터 한개의 묶음임.

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MusicalItemDto {

    private Long id; // 뮤지컬 고유 ID
    private String title; // 뮤지컬 제목
    private String period; // 공연 기간
    private String posterUrl; // 포스터 이미지 URL

}
