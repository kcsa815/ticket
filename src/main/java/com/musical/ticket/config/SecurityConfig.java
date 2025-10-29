package com.musical.ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            // 1. 페이지 접근 권한 설정
            .authorizeHttpRequests(authz -> authz
                // ▼▼▼ 모든 permitAll() 규칙을 하나로 통합 ▼▼▼
                .requestMatchers(
                    "/", 
                    "/login", 
                    "/members/new",
                    // ▼ 헤더 메뉴 (공개)
                    "/musical-list", 
                    "/rankings", 
                    "/coming-soon",
                    "/local", 
                    "/venues",
                    // ▼ 정적 리소스 (CSS, JS, 이미지)
                    "/css/**", 
                    "/js/**", 
                    "/images/**",
                    // ▼ 기타 (DB 콘솔, 임시 관리자 페이지)
                    "/h2-console/**",
                    "/admin/**"
                ).permitAll()
                
                .requestMatchers("/my_reservation").authenticated() // 내 예약은 인증 필요
                .anyRequest().authenticated() // 나머지 모든 요청은 인증 필요
            )

            // H2 콘솔 접근을 위한 헤더 설정
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            ) 
            
            // CSRF 보호 비활성화
            .csrf(csrf -> csrf.disable())

            // 3. 로그인 설정
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .usernameParameter("username")
                .passwordParameter("password")
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // 4. 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/members/logout")
                .logoutSuccessUrl("/") 
                .invalidateHttpSession(true) 
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
    
}