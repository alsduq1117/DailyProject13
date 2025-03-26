package com.example.project13.service;

import com.example.project13.controller.AuthController;
import com.example.project13.domain.User;
import com.example.project13.security.UserPrincipal;
import com.example.project13.repository.UserRepository;
import com.example.project13.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    // 회원가입
    public User signup(String email, String password) {
        User user = User.createLocalUser(
                email,
                passwordEncoder.encode(password)
        );
        return userRepository.save(user);
    }
}
