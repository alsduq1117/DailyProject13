package com.example.project13.security;

import com.example.project13.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final User user; // User 엔티티 참조
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // 생성자: User 엔티티를 기반으로 생성
    public UserPrincipal(User user) {
        this.user = user;
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    // UserDetails 인터페이스 구현 ================================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 추가 메서드: User 엔티티에 직접 접근 필요 시 사용
    public User getUser() {
        return user;
    }
}
