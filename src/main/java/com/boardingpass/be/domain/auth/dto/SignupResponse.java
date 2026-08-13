package com.boardingpass.be.domain.auth.dto;

public record SignupResponse(
    String accessToken,
    Long userId
) {
}
