package com.example.project13.security;

import com.example.project13.domain.AuthProvider;
import com.example.project13.domain.User;
import com.example.project13.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (user.getProvider() != AuthProvider.LOCAL) {
            throw new BadCredentialsException("소셜 로그인 사용자입니다.");
        }
        return new UserPrincipal(user);
    }
}
