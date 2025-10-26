package com.musical.ticket.config;
//spring security설정(비밀번호 암호화)
//절대 비밀번호를 원본 그대로 db에 저장하면 안됨.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

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
            // 1. 페이지 접근 권한 설정
            .authorizeHttpRequests(authz -> authz
                // "/", "/members/new", "/members/login" 등은 모두가 접근 가능
                .requestMatchers("/", "/members/new", "/members/login").permitAll()
                // CSS, JS, 이미지 등 정적 자원도 모두 접근 가능 (나중을 위해)
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // "/admin/**" 경로는 "ADMIN" 권한을 가진 사용자만 접근 가능
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 위에서 설정한 URL 외의 모든 요청은
                .anyRequest().authenticated() // <-- (이전 permitAll()에서 변경) 인증(로그인)된 사용자만 접근 가능
            )
            
            // 2. CSRF 보호 비활성화 (개발 초기 단계)
            .csrf(csrf -> csrf.disable())

            // 3. 로그인 설정
            .formLogin(form -> form
                .loginPage("/members/login") // 3-1. 우리가 만든 로그인 페이지 URL
                .defaultSuccessUrl("/")      // 3-2. 로그인 성공 시 이동할 기본 URL
                .usernameParameter("username") // 3-3. 로그인 폼의 아이디 input name
                .passwordParameter("password") // 3-4. 로그인 폼의 비밀번호 input name
                .failureUrl("/members/login?error=true") // 3-5. 로그인 실패 시 이동할 URL (에러 파라미터 전달)
                .permitAll()
            )

            // 4. 로그아웃 설정
            .logout(logout -> logout
                // 1. .logoutUrl() 대신 .logoutRequestMatcher() 사용 (POST 방식)
                .logoutUrl("/members/logout")
                .logoutSuccessUrl("/") 
                .invalidateHttpSession(true) 
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
    
}
