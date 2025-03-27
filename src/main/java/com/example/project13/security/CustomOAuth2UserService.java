package com.example.project13.security;

import com.example.project13.domain.AuthProvider;
import com.example.project13.domain.User;
import com.example.project13.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        AuthProvider provider = AuthProvider.valueOf(
                userRequest.getClientRegistration().getRegistrationId().toUpperCase()
        );

        String email = extractEmail(oAuth2User.getAttributes(), provider);
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerOAuth2User(provider, email));

        return new UserPrincipal(user, oAuth2User.getAttributes());
    }

    private String extractEmail(Map<String, Object> attributes, AuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> (String) attributes.get("email");
            case KAKAO -> ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
            case NAVER -> ((Map<String, Object>) attributes.get("response")).get("email").toString();
            default -> throw new IllegalArgumentException("Invalid provider");
        };
    }

    private User registerOAuth2User(AuthProvider provider, String email) {
        return userRepository.save(
                User.createOAuth2User(email, provider)
        );
    }
}
