package com.boardingpass.be.domain.bag.dto;

import jakarta.validation.constraints.NotNull;

public record ShoppingBagCreateRequest(
    @NotNull Long productSizeId
) {
}
