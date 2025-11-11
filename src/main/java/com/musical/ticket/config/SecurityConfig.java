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
                        //'OPTIONS' 메서드 요청은 인증/인가 없이 모두 허용(React 설정할 때 추가함)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 아래 경로들은 인증 없이 접근 허용 (permitAll)
                        .requestMatchers(
                                "/api/users/signup", // 회원가입
                                "/api/users/login",         // 로그인
                                "/api/musicals/**",         // 공연 정보 조회 (예시)
                                "/api/venues/**",           //공연장 조회
                                "/api/performances/**",     //공연 회차, 좌석조회
                                "/images/**",               //이미지 파일 조회
                                "/"
                        ).permitAll()

                        // 관리자(ADMIN) 권한이 필요한 경로 (예시)
                        .requestMatchers(
                                "/api/musicals/admin/**" // 예시: 공연 등록/수정/삭제
                        ).hasRole("ADMIN")

                        // 'USER' 또는 'ADMIN' 권한이 필요한 경로 (예시)
                        .requestMatchers(
                                "/api/bookings/**" // 예매하기, 예매내역 조회 등
                        ).hasAnyRole("USER", "ADMIN")

                        // 위에서 설정한 경로 외 모든 경로는 인증(로그인) 필요
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
}