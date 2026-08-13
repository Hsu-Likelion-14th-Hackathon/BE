package com.boardingpass.be.domain.auth.dto;

public record LoginResponse(
    String accessToken,
    Long userId
) {
}
