package com.example.project13.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true) // 소셜 사용자는 null 허용
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;

    // 생성 메서드
    public static User createLocalUser(String email, String encodedPassword) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.roles.add(Role.USER);
        return user;
    }
}

