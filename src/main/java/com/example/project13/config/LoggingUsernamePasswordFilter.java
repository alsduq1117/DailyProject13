package com.example.project13.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
public class LoggingUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {

    public LoggingUsernamePasswordFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("🔐 로그인 시도 - IP: {}", request.getRemoteAddr());

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        log.info("요청 파라미터 - username: {}, password: [PROTECTED]", username);

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username = super.obtainUsername(request);
        log.info("추출된 username: {}", username);
        return username != null ? username.trim() : "";
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = super.obtainPassword(request);
        log.info("추출된 password: [PROTECTED]");
        return password != null ? password : "";
    }
}