package com.example.project13.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class JwtSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public JwtSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String username = authentication.getName();
        String token = jwtUtil.generateToken(username);

        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"token\": \"%s\"}", token));
    }
}
