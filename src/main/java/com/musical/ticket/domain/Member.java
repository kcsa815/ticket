package com.musical.ticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Entity (회원 정보 테이블)
@Entity
@Getter
@Setter //보안상 최소화 하는것이 좋긴 함.
@NoArgsConstructor(access = AccessLevel.PROTECTED) //jpa는 기본생성자가 필요함.
public class Member {
    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DB아이디가 자동 증가
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username; //로그인아이디

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String name; //사용자 이름

    @Enumerated(EnumType.STRING)//Enum타입을 문자열로 저장(USER, ADMIN)
    @Column(nullable = false)
    private Role role;

    //빌더패턴 : 객체 생성을 깔끔하게 처리해줌
    @Builder
    public Member(String username, String password, String name, Role role){
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
    }
    
}