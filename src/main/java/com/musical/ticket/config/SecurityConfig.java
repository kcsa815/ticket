package com.musical.ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/images/**"); 
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 2. CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(authz -> authz
                // 3. OPTIONS (Preflight) 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 

                // 4. 로그인 / 회원가입 (POST만)
                .requestMatchers(
                    HttpMethod.POST, 
                    "/api/users/signup", 
                    "/api/users/login"
                ).permitAll()

                // 5. 모든 GET 조회 요청 허용 (토큰 불필요)
                .requestMatchers(
                    HttpMethod.GET, 
                    "/api/musicals/**",
                    "/api/venues/**",
                    "/api/performances/**",
                    "/", // Root path
                    "/error" // Error path
                ).permitAll()

                // 6. ADMIN 전용 (POST/PUT/DELETE)
                .requestMatchers(
                    HttpMethod.POST, "/api/musicals/**", "/api/venues/**", "/api/performances/**"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.PUT, "/api/musicals/**"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.DELETE, "/api/musicals/**"
                ).hasRole("ADMIN")

                // 7. USER/ADMIN 모두 허용 (예매, 내 정보)
                .requestMatchers(
                    "/api/bookings/**", 
                    "/api/users/me"     
                ).hasAnyRole("USER", "ADMIN")

                // 8. 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )

            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 9. 로컬 주소와 배포 주소 명시적 허용
        config.addAllowedOrigin("http://localhost:5173");      
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // GET, POST, OPTIONS, etc.
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}