package com.musical.ticket.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

//Enum(사용자 권한 : 일반유저, 관리자)
@Getter
@RequiredArgsConstructor
public enum Role {
    //spring security에서는 권한 앞에 "ROLE_" 접두사가 붙어야 한다.
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
    
}
