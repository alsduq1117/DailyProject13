package com.example.project13.service;

import com.example.project13.domain.User;
import com.example.project13.repository.UserRepository;
import com.example.project13.request.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long createUser(SignupRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder().username(request.getUsername()).password(encodedPassword).build();
        return userRepository.save(user).getId();
    }
}
