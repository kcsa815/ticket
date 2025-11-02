package com.musical.ticket.config.jwt;
//jwt인증필터 : 모든 HTTP 요청이 Controller에 도달하기 전에 통과하는 '관문(Filter)'.요청 헤더의 토큰을 검사함.

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter: 모든 요청마다 딱 한 번만 실행되는 필터
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer "; // 공백 주의

    private final JwtTokenProvider jwtTokenProvider;

    // 실제 필터링 로직
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = resolveToken(request);

        // validateToken 호출 시 request를 넘겨줌
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt, request)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // validateToken에서 false가 반환 (예외가 발생)되면,
        // 여기서 SecurityContextHolder에 저장하지 않고 바로 filterChain.doFilter로 넘어감.
        // 그러면 SecurityConfig에 등록된 AuthenticationEntryPoint가 작동함.

        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보(Bearer)를 꺼내오는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7); // "Bearer " (7글자) 이후의 토큰 값 반환
        }
        return null;
    }
}
