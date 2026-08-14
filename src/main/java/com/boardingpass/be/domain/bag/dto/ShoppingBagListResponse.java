package com.boardingpass.be.domain.bag.dto;

import java.util.List;

public record ShoppingBagListResponse(
    List<ShoppingBagItemResponse> items,
    int totalCount
) {
  public static ShoppingBagListResponse of(List<ShoppingBagItemResponse> items) {
    return new ShoppingBagListResponse(items, items.size());
  }
}
