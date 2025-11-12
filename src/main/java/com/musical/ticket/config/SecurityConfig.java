package com.musical.ticket.config;

// 시큐리티 단계에 필요한 만든 것 모두 연결
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.musical.ticket.config.jwt.JwtAuthenticationFilter;
import com.musical.ticket.config.jwt.JwtTokenProvider;
import com.musical.ticket.handler.security.CustomAccessDeniedHandler;
import com.musical.ticket.handler.security.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean // 4단계에서 만든 PasswordEncoder Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 7단계에서 사용할 AuthenticationManager Bean
    // (UserService에서 로그인 시 인증을 처리하기 위해 필요)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //cors 설정 맨 위에 추가('예매 취소'가 제대로 작동 안해서 )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 1. CSRF, Form Login, HTTP Basic 비활성화 (Stateless API이므로)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 2. 세션 관리 정책: STATELESS (세션을 사용하지 않음)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers ->
                        headers.xssProtection(xss -> xss.disable())
                )
                
                // 3. URL별 권한 설정 (인가)
                .authorizeHttpRequests(authz -> authz

                        // **허용 경로**
                        .requestMatchers(
                                HttpMethod.GET,                 //GET요청은 대부분 허용
                                "/api/musicals/**",         // 공연 정보 조회 (예시)
                                "/api/venues/**",           //공연장 조회
                                "/api/performances/**",     //공연 회차, 좌석조회
                                "/images/**",               //이미지 파일 조회
                                "/"
                        ).permitAll()
                        .requestMatchers(
                                "/api/users/signup", // 회원가입
                                "/api/users/login"      // 로그인
                        ).permitAll()

                        // ** 관리자 경로 **
                        .requestMatchers(
                                HttpMethod.POST, 
                                "/api/musicals/**",
                                "/api/venues/**",
                                "/api/performances/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/musicals/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/musicals/**"
                        ).hasRole("ADMIN")

                        // 최하순위 : 위에서 걸러지지 않은 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated())

                // 4. JWT 인증 필터 추가
                // 만들었던 JwtAuthenticationFilter를
                // UsernamePasswordAuthenticationFilter (기본 로그인 필터) 보다 앞에 배치
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)

                // 5. 예외 핸들링 추가
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 401 처리
                        .accessDeniedHandler(customAccessDeniedHandler)         // 403 처리
                );

        return http.build();
    }

    /*
     * CORS 설정을 SecurityConfig에 통합
     * (WebConfig의 addCorsMappings를 대체)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173"); //React 앱 주소
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}