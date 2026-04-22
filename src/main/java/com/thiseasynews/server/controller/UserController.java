package com.thiseasynews.server.controller;

import com.thiseasynews.server.dto.response.UserMeResponse;
import com.thiseasynews.server.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(ApiResponse.fail("UNAUTHORIZED", "로그인이 필요합니다."));
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oauth2User)) {
            return ResponseEntity.status(401).body(ApiResponse.fail("UNAUTHORIZED", "OAuth 사용자 정보가 없습니다."));
        }

        Map<String, Object> attrs = oauth2User.getAttributes();

        String email = null;
        String name = null;
        String profileImageUrl = null;
        Long id = null;

        if (attrs.containsKey("sub")) {
            id = Math.abs((long) ((String) attrs.get("sub")).hashCode());
            email = (String) attrs.get("email");
            name = (String) attrs.get("name");
            profileImageUrl = (String) attrs.get("picture");
        } else if (attrs.containsKey("response")) {
            Map<String, Object> response = castMap(attrs.get("response"));
            id = response != null && response.get("id") != null
                    ? Math.abs((long) String.valueOf(response.get("id")).hashCode())
                    : null;
            email = response != null ? (String) response.get("email") : null;
            name = response != null ? (String) response.get("name") : null;
            profileImageUrl = response != null ? (String) response.get("profile_image") : null;
        } else if (attrs.containsKey("id")) {
            id = Math.abs((long) String.valueOf(attrs.get("id")).hashCode());
            Map<String, Object> kakaoAccount = castMap(attrs.get("kakao_account"));
            Map<String, Object> profile = kakaoAccount != null
                    ? castMap(kakaoAccount.get("profile"))
                    : null;

            email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            name = profile != null ? (String) profile.get("nickname") : null;
            profileImageUrl = profile != null ? (String) profile.get("profile_image_url") : null;
        }

        return ResponseEntity.ok(
                ApiResponse.ok(
                        UserMeResponse.builder()
                                .id(id)
                                .email(email)
                                .name(name)
                                .nickname(name)
                                .profileImageUrl(profileImageUrl)
                                .onboardingCompleted(false)
                                .build()
                )
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        if (value == null) return null;
        return (Map<String, Object>) value;
    }
}