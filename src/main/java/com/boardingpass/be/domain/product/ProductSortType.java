package com.boardingpass.be.domain.product;

public enum ProductSortType {
  POPULARITY, PRICE_ASC, PRICE_DESC;

  public static ProductSortType from(String raw) {
    if (raw == null || raw.isBlank()) {
      return POPULARITY;
    }
    try {
      return ProductSortType.valueOf(raw.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      return POPULARITY;
    }
  }
}
