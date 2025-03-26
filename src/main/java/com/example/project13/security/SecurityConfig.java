package com.example.project13.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity  // Spring Security 활성화
@EnableMethodSecurity  // 메소드 수준 보안 활성화(ex. @PreAuthorize)
@RequiredArgsConstructor  // final 필드 자동 생성자 주입
@Slf4j  // 로깅 사용
public class SecurityConfig {

    // JWT 인증 필터 (요청 시 JWT 검증)
    private final JwtAuthenticationFilter jwtAuthFilter;
    // JWT 인증 실패 핸들러
    private final JwtAuthenticationEntryPoint jwtEntryPoint;
    // JWT 유틸리티 (토큰 생성/검증)
    private final JwtUtil jwtUtil;
    // 사용자 상세 서비스 (DB에서 사용자 정보 조회)
    private final CustomUserDetailsService userDetailsService;
    //    // OAuth2 로그인 성공 핸들러
//    private final OAuth2LoginSuccessHandler oAuth2SuccessHandler;
//    // OAuth2 사용자 서비스 (소셜 로그인 사용자 처리)
//    private final CustomOAuth2UserService oAuth2UserService;
    // JSON 변환기
    private final ObjectMapper objectMapper;

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailHandler loginFailHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigSource()))
                // CSRF 비활성화 (JWT 사용 시 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                // HTTP 기본 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 폼 로그인 비활성화 (REST API 방식)
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션 사용 안함 (STATELESS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 허용할 경로
                        .requestMatchers("/", "/auth/**",       // 인증 관련 엔드포인트
                                "/oauth2/**",     // OAuth2 관련 엔드포인트
                                "/error",         // 에러 핸들링
                                "/swagger-ui/**", // API 문서
                                "/v3/api-docs/**").permitAll()
                        // ADMIN 역할만 접근 가능한 경로
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated())
                // OAuth2 로그인 설정
//                .oauth2Login(oauth2 -> oauth2
//                        /**
//                         * - 예시: Google 로그인 시작 → /oauth2/authorization/google
//                         */
//                        .authorizationEndpoint(authorization -> authorization
//                                .baseUri("/oauth2/authorization")
//                        )
//
//                        /**
//                         * - 예시: Google 리다이렉트 → /login/oauth2/code/google
//                         */
//                        .redirectionEndpoint(redirection -> redirection
//                                .baseUri("/login/oauth2/code/*")
//                        )
//
//                        /**
//                         * [사용자 정보 조회 설정]
//                         * - 인증 성공 후 사용자 상세 정보 로드
//                         * - CustomOAuth2UserService에서 처리할 작업:
//                         *   1. 소셜 사용자 정보를 기반으로 회원 가입/업데이트
//                         *   2. 권한 정보 설정
//                         *   3. 커스텀 UserDetails 객체 생성
//                         */
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(oAuth2UserService)
//                        )
//                        .successHandler(oAuth2SuccessHandler)
//                )
                // 예외 처리
                .exceptionHandling(ex -> ex
                        // 인증 실패 시 처리
                        .authenticationEntryPoint(jwtEntryPoint)
                        // 인가 실패 시 처리
                        .accessDeniedHandler(new JwtAccessDeniedHandler(objectMapper)))
                // JWT 인증 필터 추가
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // 일반 로그인 필터 추가
                .addFilterBefore(emailPasswordAuthFilter(authenticationManager(), objectMapper), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 일반 로그인 필터 빈 등록
    @Bean
    public EmailPasswordAuthFilter emailPasswordAuthFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) throws Exception {
        EmailPasswordAuthFilter filter = new EmailPasswordAuthFilter(new AntPathRequestMatcher("/auth/login", "POST"), authenticationManager, objectMapper);
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        filter.setAuthenticationFailureHandler(loginFailHandler);
        return filter;
    }

    // 인증 관리자 구성
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        // DB 기반 인증 제공자 생성
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        // 사용자 상세 서비스 설정
        daoProvider.setUserDetailsService(userDetailsService);
        // 패스워드 인코더 설정
        daoProvider.setPasswordEncoder(passwordEncoder());

        // 인증 제공자 등록
        return new ProviderManager(daoProvider);
    }

    // 실무용 CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용할 오리진 목록
        config.setAllowedOrigins(Arrays.asList("https://production.com",  // 운영 도메인
                "http://localhost:3000"   // 개발용 로컬호스트
        ));
        // 허용할 HTTP 메소드
        config.setAllowedMethods(Arrays.asList("*"));
        // 허용할 헤더
        config.setAllowedHeaders(Arrays.asList("*"));
        // 노출할 헤더
        config.setExposedHeaders(Arrays.asList("Authorization", "X-Refresh-Token"));
        // 자격 증명 허용
        config.setAllowCredentials(true);
        // 캐시 지속 시간(초)
        config.setMaxAge(3600L);

        // 모든 경로에 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 패스워드 인코더 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 해시 알고리즘 사용 (강도 12)
        return new BCryptPasswordEncoder(12); // 실무에서는 10~12 strength 권장
    }

    // 웹 보안 커스터마이저 (보안 예외 적용 경로)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                // 보안 필터 적용 제외할 경로
                .requestMatchers("/favicon.ico",    // 파비콘
                        "/static/**",      // 정적 리소스
                        "/resources/**",   // 리소스
                        "/h2-console/**"   // H2 데이터베이스 콘솔 (개발용)
                );
    }
}

