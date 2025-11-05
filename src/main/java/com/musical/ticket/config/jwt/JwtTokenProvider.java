package com.musical.ticket.config.jwt;
//토큰 관련 모든 작업을 처리하는 핵심 클래스

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.musical.ticket.dto.security.TokenDto;
import com.musical.ticket.handler.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    // application.properties에서 설정값 주입
    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationTime,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationTime) {
        
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8); 
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    // 1. 인증 정보(Authentication)를 기반으로 AccessToken, RefreshToken 생성
    public TokenDto generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationTime);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       // email
                .claim("auth", authorities)                // 권한 (ROLE_USER, ROLE_ADMIN)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성 (만료 시간만 다름)
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpirationTime)
                .build();
    }

    // 2. JWT 토큰을 복호화하여 토큰에 담긴 인증 정보(Authentication)를 꺼냄
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임(claims)에서 권한 정보(auth) 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 3. 토큰 정보를 검증
    public boolean validateToken(String token, HttpServletRequest request) { // request 추가
    try {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
        log.info("잘못된 JWT 서명입니다.");
        request.setAttribute("exception", ErrorCode.TOKEN_INVALID); // 예외 설정
    } catch (ExpiredJwtException e) {
        log.info("만료된 JWT 토큰입니다.");
        request.setAttribute("exception", ErrorCode.TOKEN_EXPIRED); // 예외 설정
    } catch (UnsupportedJwtException e) {
        log.info("지원되지 않는 JWT 토큰입니다.");
        request.setAttribute("exception", ErrorCode.TOKEN_INVALID); // 예외 설정
    } catch (IllegalArgumentException e) {
        log.info("JWT 토큰이 잘못되었습니다.");
        request.setAttribute("exception", ErrorCode.TOKEN_INVALID); // 예외 설정
    }
    return false;
}

    // AccessToken에서 Claims(정보) 추출
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이어도 Claims는 꺼내야 함 (예: Refresh Token 교체 시)
            return e.getClaims();
        }
    }
}
