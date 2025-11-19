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
            // (CORS 설정 적용)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(authz -> authz
                // 1. "Preflight" (OPTIONS) 요청은 무조건 허용 (CORS 필수)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 2. 로그인/회원가입/조회 등 "공개" 경로
                .requestMatchers(
                        "/api/users/signup",
                        "/api/users/login",
                        "/api/musicals/**",
                        "/api/venues/**",
                        "/api/performances/**",
                        "/images/**",
                        "/"
                ).permitAll()

                // 3. 나머지(예매 등)는 인증 필요
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
        config.addAllowedOrigin("https://ticket-frontend-swart.vercel.app"); 
        // (로컬 테스트용)
        config.addAllowedOrigin("http://localhost:5173");
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