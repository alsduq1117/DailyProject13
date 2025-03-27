package com.example.project13.controller;

import com.example.project13.domain.User;
import com.example.project13.security.UserPrincipal;
import com.example.project13.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    // 인증 없이 접근 가능
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok(testService.getPublicMessage());
    }

    // USER 권한 필요
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok(testService.getUserMessage());
    }

    // ADMIN 권한 필요
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok(testService.getAdminMessage());
    }

    // 인증된 모든 사용자 접근 가능
    @GetMapping("/all-authenticated")
    public ResponseEntity<String> allAuthenticated() {
        return ResponseEntity.ok(testService.getCommonMessage());
    }

    // 현재 인증된 사용자 상세 정보 조회
    @GetMapping("/me/details")
    public ResponseEntity<Map<String, Object>> getMyDetails(@AuthenticationPrincipal UserPrincipal principal) {
        User user = principal.getUser();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("email", user.getEmail());
        response.put("roles", user.getRoles());
        response.put("provider", user.getProvider());

        return ResponseEntity.ok(response);
    }
}
