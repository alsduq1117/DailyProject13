//package com.example.project13.security;
//
//import com.example.project13.domain.User;
//import com.example.project13.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        // 기본 OAuth2UserService 생성
//        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//
//        // registrationId 추출 (google, kakao, naver 등)
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        // OAuth2 로그인 시 키가 되는 값 (구글: sub, 카카오: id, 네이버: response)
//        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
//
//        // 소셜 로그인 사용자 정보 DTO
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체 생성
//        UserProfile userProfile = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
//
//        // 사용자 이메일 추출
//        String email = userProfile.getEmail();
//        if (email == null || email.isEmpty()) {
//            throw new OAuth2AuthenticationException("이메일 정보를 가져올 수 없습니다.");
//        }
//
//        // DB에서 사용자 조회 또는 생성
//        User user = userRepository.findByEmail(email).orElseGet(() -> createSocialUser(userProfile, registrationId));
//
//        return new CustomOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())), attributes, userNameAttributeName, user.getEmail(), user.getRole());
//    }
//
//    private User createSocialUser(UserProfile userProfile, String provider) {
//        User user = User.builder().email(userProfile.getEmail()).password(passwordEncoder.encode(UUID.randomUUID().toString())) // 임시 비밀번호
//                .provider(AuthProvider.valueOf(provider.toUpperCase())).role(Role.USER).build();
//        return userRepository.save(user);
//    }
//}
