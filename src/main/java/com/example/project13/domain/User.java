package com.example.project13.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

//    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    // OAuth2 사용자 생성 메서드
    public static User createOAuth2User(String email, AuthProvider provider) {
        User user = new User();
        user.email = email;
        user.provider = provider;
        user.roles.add(Role.USER);
        return user;
    }

    // 일반 사용자 생성 메서드
    public static User createLocalUser(String email, String password) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.provider = AuthProvider.LOCAL;
        user.roles.add(Role.USER);
        return user;
    }
}
