package com.musical.ticket.config;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
            
            // --- 👇 [핵심!] 로그인/회원가입/조회는 모두 허용 ---
            .authorizeHttpRequests(authz -> authz
                // 1. OPTIONS, 메인, 에러 페이지 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/error").permitAll()

                // 2. 로그인 / 회원가입 (POST만)
                .requestMatchers(
                    HttpMethod.POST, 
                    "/api/users/signup", 
                    "/api/users/login"
                ).permitAll()

                // 3. 모든 GET 조회 요청 허용 (토큰 불필요)
                .requestMatchers(
                    HttpMethod.GET, 
                    "/api/musicals/**",
                    "/api/venues/**",
                    "/api/performances/**",
                    "/images/**"
                ).permitAll()

                // 4. ADMIN 전용 (POST/PUT/DELETE)
                .requestMatchers(
                    HttpMethod.POST, "/api/musicals/**", "/api/venues/**", "/api/performances/**"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.PUT, "/api/musicals/**"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.DELETE, "/api/musicals/**"
                ).hasRole("ADMIN")

                // 5. USER/ADMIN 모두 허용 (예매, 내 정보)
                .requestMatchers(
                    "/api/bookings/**", 
                    "/api/users/me"     
                ).hasAnyRole("USER", "ADMIN")

                // 6. 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            // --- 👆 ---

            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
            );

        return http.build();
    }

    /*
    * CORS 설정을 SecurityConfig에 통합
    */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("https://ticket-frontend-swart.vercel.app"); 
        config.addAllowedOrigin("http://localhost:5173"); 
        
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}