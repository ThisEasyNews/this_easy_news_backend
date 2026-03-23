package com.thiseasynews.server.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration
public class JpaConfig {
    // JPA Auditing 활성화 → BaseTimeEntity의 @CreatedDate / @LastModifiedDate 동작
}
