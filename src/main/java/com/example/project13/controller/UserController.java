package com.example.project13.controller;

import com.example.project13.request.SignupRequest;
import com.example.project13.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@RequestBody SignupRequest request) {
        Long userId = userService.createUser(request);
        return ResponseEntity.ok(userId);
    }
}