package com.musical.ticket.handler.security;
//Spring Security 전용 핸들러 생성 (401 처리)
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musical.ticket.handler.ErrorResponse;
import com.musical.ticket.handler.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

// 401 (Unauthorized) - 인증(Authentication) 실패 시
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("Authentication Failed: {}", authException.getMessage());
        
        // ErrorCode에서 AUTHENTICATION_FAILED 가져오기
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
        
        // 5단계에서 토큰 만료/유효하지 않음(JwtTokenProvider)에서 던진 예외를 확인
        if (request.getAttribute("exception") != null) {
            errorCode = (ErrorCode) request.getAttribute("exception");
        }

        ErrorResponse errorResponse = new ErrorResponse(errorCode);

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
