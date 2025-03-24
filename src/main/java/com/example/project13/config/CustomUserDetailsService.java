package com.example.project13.config;

import com.example.project13.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername 실행");
        return userRepository.findByUsername(username)
                .map(user -> {
                    log.info("DB 사용자 정보: {}", user.getUsername());
                    // Spring Security의 User 클래스 사용
                    return org.springframework.security.core.userdetails.User.builder()
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .build();
                })
                .orElseThrow(() -> {
                    log.info("사용자 없음: {}", username);
                    return new UsernameNotFoundException("Not found: " + username);
                });
    }
}