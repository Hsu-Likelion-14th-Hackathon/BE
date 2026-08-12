package com.boardingpass.be.domain.wishlist.dto;

import java.util.List;

public record WishlistListResponse(
    List<WishlistItemResponse> items,
    int totalCount
) {
  public static WishlistListResponse of(List<WishlistItemResponse> items) {
    return new WishlistListResponse(items, items.size());
  }
}
