package com.thiseasynews.server.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMeResponse {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private boolean onboardingCompleted;
}