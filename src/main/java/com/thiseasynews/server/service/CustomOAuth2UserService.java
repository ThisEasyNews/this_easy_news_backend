package com.thiseasynews.server.service;

import com.thiseasynews.server.entity.User;
import com.thiseasynews.server.entity.UserAuthProvider;
import com.thiseasynews.server.repository.UserAuthProviderRepository;
import com.thiseasynews.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        OAuthUserInfo userInfo = extractUserInfo(provider, attributes);

        User user = userAuthProviderRepository
                .findByProviderTypeAndProviderUserId(provider, userInfo.providerUserId())
                .map(UserAuthProvider::getUser)
                .orElseGet(() -> createUser(provider, userInfo));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return oauth2User;
    }

    private User createUser(String provider, OAuthUserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.email())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(userInfo.email())
                                .name(userInfo.name())
                                .nickname(userInfo.name())
                                .profileImageUrl(userInfo.profileImageUrl())
                                .statusCode("active")
                                .roleCode("user")
                                .lastLoginAt(LocalDateTime.now())
                                .build()
                ));
                userAuthProviderRepository.save(
                        UserAuthProvider.builder()
                                .user(user)
                                .providerType(provider)
                                .providerUserId(userInfo.providerUserId())
                                .providerEmail(userInfo.email())
                                .lastLoginAt(LocalDateTime.now())
                                .build()
                );

        return user;
    }

    private OAuthUserInfo extractUserInfo(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> {
                String id = (String) attributes.get("sub");
                String name = (String) attributes.get("name");
                String email = (String) attributes.get("email");
                String picture = (String) attributes.get("picture");
                yield new OAuthUserInfo(id, name, email, picture);
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                String id = (String) response.get("id");
                String name = (String) response.get("name");
                String email = (String) response.get("email");
                String picture = (String) response.get("profile_image");
                yield new OAuthUserInfo(id, name, email, picture);
            }
            case "kakao" -> {
                String id = String.valueOf(attributes.get("id"));

                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = kakaoAccount != null
                        ? (Map<String, Object>) kakaoAccount.get("profile")
                        : null;

                String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
                String name = profile != null ? (String) profile.get("nickname") : "카카오사용자";
                String picture = profile != null ? (String) profile.get("profile_image_url") : null;

                yield new OAuthUserInfo(id, name, email, picture);
            }
            default -> throw new OAuth2AuthenticationException(new OAuth2Error("invalid_provider"));
        };
    }

    private record OAuthUserInfo(
            String providerUserId,
            String name,
            String email,
            String profileImageUrl
    ) {}
}