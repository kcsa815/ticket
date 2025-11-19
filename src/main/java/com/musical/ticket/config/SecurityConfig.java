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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.xssProtection(xss -> xss.disable()))

                .authorizeHttpRequests(authz -> authz
                    
                    // (1순위) "허용"할 경로들 (인증 불필요)
                    .requestMatchers(
                            // (A) 로그인/회원가입
                            "/api/users/signup",
                            "/api/users/login"
                    ).permitAll()
                    .requestMatchers(
                            // (B) 모든 GET 조회 요청 (이미지 포함)
                            HttpMethod.GET, 
                            "/api/musicals/**",
                            "/api/venues/**",
                            "/api/performances/**",
                            "/images/**",
                            "/"
                    ).permitAll()
                    
                    // (2순위) "ADMIN"만 허용할 경로
                    .requestMatchers(
                            HttpMethod.POST, "/api/musicals/**", "/api/venues/**", "/api/performances/**"
                    ).hasRole("ADMIN")
                    .requestMatchers(
                            HttpMethod.PUT, "/api/musicals/**"
                    ).hasRole("ADMIN")
                    .requestMatchers(
                            HttpMethod.DELETE, "/api/musicals/**"
                    ).hasRole("ADMIN")
                    
                    // (3순위) "USER" (또는 ADMIN)만 허용할 경로
                    // (예: 예매하기, 예매 취소, 내 정보 보기)
                    .requestMatchers(
                            "/api/bookings/**", // 예매/취소/내역
                            "/api/users/me"     // 내 정보
                    ).hasAnyRole("USER", "ADMIN")

                    // (최하순위) 위에서 걸러지지 않은 '나머지 모든' 요청은 인증 필요
                    .anyRequest().authenticated()
                )


                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
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
        config.addAllowedOrigin("https://musical-front.vercel.app"); //vercel로 배포된 React 앱 주소
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