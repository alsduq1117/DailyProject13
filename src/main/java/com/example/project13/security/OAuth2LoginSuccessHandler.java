//package com.example.project13.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final JwtUtil jwtUtil;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void onAuthenticationSuccess(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Authentication authentication
//    ) throws IOException {
//        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//
//        // JWT 토큰 생성
//        String accessToken = jwtUtil.generateToken(oAuth2User);
//        String refreshToken = jwtUtil.generateRefreshToken(oAuth2User);
//
//        // 응답 설정
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//
//        Map<String, Object> tokenResponse = new HashMap<>();
//        tokenResponse.put("access_token", accessToken);
//        tokenResponse.put("refresh_token", refreshToken);
//        tokenResponse.put("expires_in", jwtUtil.getAccessTokenExpiration());
//
//        objectMapper.writeValue(response.getWriter(), tokenResponse);
//    }
//}
