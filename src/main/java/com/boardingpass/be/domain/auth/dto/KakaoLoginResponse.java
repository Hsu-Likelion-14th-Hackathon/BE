package com.boardingpass.be.domain.auth.dto;

public record KakaoLoginResponse(
    String accessToken,
    Boolean isNewUser,
    Long userId,
    Boolean profileCompleted
) {
}
