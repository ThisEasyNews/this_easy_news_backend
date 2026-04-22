package com.thiseasynews.server.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthDebugRunner implements CommandLineRunner {

    private final Environment environment;

    @Override
    public void run(String... args) {
        String naverClientId = environment.getProperty(
                "spring.security.oauth2.client.registration.naver.client-id",
                "NOT_FOUND"
        );

        String naverRedirectUri = environment.getProperty(
                "spring.security.oauth2.client.registration.naver.redirect-uri",
                "NOT_FOUND"
        );

        log.info("=== NAVER CLIENT ID === {}", naverClientId);
        log.info("=== NAVER REDIRECT URI === {}", naverRedirectUri);
    }
}