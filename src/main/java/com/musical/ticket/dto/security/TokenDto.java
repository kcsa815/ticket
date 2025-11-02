package com.musical.ticket.dto.security;
//로그인 시 클라이언트에게 반환할 dto
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    
    private String grantType; // "Bearer" (토큰 타입)
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
}
