package com.musical.ticket.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.musical.ticket.handler.exception.CustomException;
import com.musical.ticket.handler.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityUtil {
    //private 생성자로 객체 생성 방지
    private SecurityUtil(){}

    public static String getCurrentUserEmail(){
        //1. SecurityContext에서 Authentication객체를 가져옴
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //2. 인증 정보가 없거나, 인증되지 않은 사용자(annoymousUser)인지 확인
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName()==null || 
            authentication.getName().equals("anonymousUser")) {
            
            log.warn("Security Context에 유효한 인증 정보가 없습니다.");
            // 6단계(handler)에서 만든 CustomException 활용
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        //3. 인증된 사용자의 이메일(getName -> UserDetails의 getUsername  반환)
        //5단계 CustomUserDtailsService에서 email을 username으로 사용했음.
        return authentication.getName();
    }
}
