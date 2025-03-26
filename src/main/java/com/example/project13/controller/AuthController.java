package com.example.project13.controller;

import com.example.project13.domain.User;
import com.example.project13.request.LoginRequest;
import com.example.project13.request.SignupRequest;
import com.example.project13.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        User user = authService.signup(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new SignupResponse(user.getId(), user.getEmail()));
    }


    public record SignupResponse(Long id, String email) {}
}
