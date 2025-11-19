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
            
            // --- 👇👇👇 [핵심 수정!] HTTP 메서드별로 분리 ---
            .authorizeHttpRequests(authz -> authz
                
                // 1. (가장 먼저!) OPTIONS, 메인, 에러 페이지 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS Preflight 허용
                .requestMatchers("/", "/error").permitAll() // Health check 및 에러 페이지 허용

                // 2. (두 번째!) 로그인 / 회원가입 (POST만)
                .requestMatchers(
                    HttpMethod.POST, 
                    "/api/users/signup", 
                    "/api/users/login"
                ).permitAll() // POST 메서드 명시

                // 3. (세 번째!) 모든 GET 조회 요청 허용 (토큰 불필요)
                .requestMatchers(
                    HttpMethod.GET, 
                    "/api/musicals/**",
                    "/api/venues/**",
                    "/api/performances/**",
                    "/images/**"
                ).permitAll()

                // 4. (네 번째!) ADMIN 전용 (POST/PUT/DELETE)
                .requestMatchers(
                    HttpMethod.POST, "/api/musicals/**", "/api/venues/**", "/api/performances/**"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.PUT, "/api/musicals/**"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.DELETE, "/api/musicals/**"
                ).hasRole("ADMIN")

                // 5. (다섯 번째!) USER/ADMIN 모두 허용 (예매, 내 정보)
                .requestMatchers(
                    "/api/bookings/**", // 예매/취소/내역
                    "/api/users/me"     // 내 정보 조회
                ).hasAnyRole("USER", "ADMIN")

                // 6. (최하순위!) 위에서 걸러지지 않은 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )

            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        //특정 주소 대신 "모두 허용" 패턴 사용
        config.addAllowedOriginPattern("*"); 
        config.addAllowedOrigin("https://ticket-frontend-swart.vercel.app");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}