package com.boardingpass.be.domain.fitting.dto;

import jakarta.validation.constraints.NotNull;

public record FittingSessionCreateRequest(
    @NotNull Long productColorId,
    String fileKey
) {
}
