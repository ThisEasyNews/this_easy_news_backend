package com.thiseasynews.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", true);
            response.put("message", "anonymous");
            response.put("data", Map.of(
                    "authenticated", false
            ));
            return ResponseEntity.ok(response);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oauth2User)) {
            response.put("success", true);
            response.put("message", "authenticated");
            response.put("data", Map.of(
                    "authenticated", true,
                    "name", authentication.getName()
            ));
            return ResponseEntity.ok(response);
        }

        Map<String, Object> attrs = oauth2User.getAttributes();

        response.put("success", true);
        response.put("message", "authenticated");
        response.put("data", Map.of(
                "authenticated", true,
                "name", extractName(attrs),
                "email", extractEmail(attrs),
                "providerUserId", extractProviderUserId(attrs)
        ));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        response.setHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly; Max-Age=0");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "logout success");
        result.put("data", null);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/login/{provider}")
    public void login(@PathVariable String provider, HttpServletResponse response) throws IOException {
        if (!provider.equals("naver") && !provider.equals("google") && !provider.equals("kakao")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원하지 않는 provider 입니다.");
            return;
        }

        response.sendRedirect("/oauth2/authorization/" + provider);
    }

    private String extractName(Map<String, Object> attrs) {
        if (attrs.containsKey("sub")) {
            return (String) attrs.get("name");
        }

        if (attrs.containsKey("response")) {
            Map<?, ?> response = (Map<?, ?>) attrs.get("response");
            return (String) response.get("name");
        }

        if (attrs.containsKey("id")) {
            Map<?, ?> kakaoAccount = (Map<?, ?>) attrs.get("kakao_account");
            if (kakaoAccount != null) {
                Map<?, ?> profile = (Map<?, ?>) kakaoAccount.get("profile");
                if (profile != null) {
                    return (String) profile.get("nickname");
                }
            }
        }

        return null;
    }

    private String extractEmail(Map<String, Object> attrs) {
        if (attrs.containsKey("sub")) {
            return (String) attrs.get("email");
        }

        if (attrs.containsKey("response")) {
            Map<?, ?> response = (Map<?, ?>) attrs.get("response");
            return (String) response.get("email");
        }

        if (attrs.containsKey("id")) {
            Map<?, ?> kakaoAccount = (Map<?, ?>) attrs.get("kakao_account");
            if (kakaoAccount != null) {
                return (String) kakaoAccount.get("email");
            }
        }

        return null;
    }

    private String extractProviderUserId(Map<String, Object> attrs) {
        if (attrs.containsKey("sub")) {
            return (String) attrs.get("sub");
        }

        if (attrs.containsKey("response")) {
            Map<?, ?> response = (Map<?, ?>) attrs.get("response");
            return (String) response.get("id");
        }

        if (attrs.containsKey("id")) {
            return String.valueOf(attrs.get("id"));
        }

        return null;
    }
}