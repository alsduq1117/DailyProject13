package com.example.project13.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(principal);
        String refreshToken = jwtUtil.generateRefreshToken(principal);

        // JSON 응답
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_OK);
        body.put("message", "로그인 성공");
        body.put("email", principal.getUsername());
        body.put("accessToken", accessToken);
        body.put("refreshToken", refreshToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
