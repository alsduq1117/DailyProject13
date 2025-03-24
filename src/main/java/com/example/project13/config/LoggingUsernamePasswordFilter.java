package com.example.project13.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
public class LoggingUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {

    public LoggingUsernamePasswordFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("[1/4] UsernamePasswordFilter: 로그인 요청 수신 (IP: {})", request.getRemoteAddr());

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        log.info("추출 정보 - 사용자명: {}, 비밀번호 길이: {}", username, password != null ? password.length() : 0);

        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}