package com.boardingpass.be.domain.wishlist.dto;

import jakarta.validation.constraints.NotNull;

public record WishlistCreateRequest(
    @NotNull Long productColorId
) {
}
