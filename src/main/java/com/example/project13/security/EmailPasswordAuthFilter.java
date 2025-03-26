package com.example.project13.security;

import com.example.project13.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

public class EmailPasswordAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public
    EmailPasswordAuthFilter(RequestMatcher requiresAuth, AuthenticationManager manager, ObjectMapper objectMapper) {
        super(requiresAuth, manager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 요청 처리 실패", e);
        }
    }
}
