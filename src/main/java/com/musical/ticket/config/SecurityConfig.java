package com.musical.ticket.config;
//spring security설정(비밀번호 암호화)
//절대 비밀번호를 원본 그대로 db에 저장하면 안됨.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //spring security 설정을 활성화
public class SecurityConfig {

    //1. 비밀번호 암호화 객체(PasswordEncoder)를 Bean으로 등록
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //BCrypt 해시 함수를 사용하는 인코더

    }

    //2. SecurityFilterChain을 Bean으로 등록(보안 규칙 설정)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            //일단 CSRF 보호 비활성화(개발 초기)
            .csrf(csrf -> csrf.disable())

            //모든 http요청에 대해
            .authorizeHttpRequests(authz -> authz
                // .anyRequest().authenticated()  <--나중에 이걸로 바꿔야됨.
                .anyRequest().permitAll() //지금은 모든 요청 허용
            );

        return http.build();
    }
    

}
