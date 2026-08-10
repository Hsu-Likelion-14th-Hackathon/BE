package com.boardingpass.be.domain.product.dto;

import com.boardingpass.be.domain.product.ProductSize;

public record ProductSizeResponse(
    Long productSizeId,
    String sizeLabel,
    String sizeNote,
    Integer stock
) {
  public static ProductSizeResponse from(ProductSize size) {
    return new ProductSizeResponse(
        size.getId(),
        size.getSizeLabel(),
        size.getSizeNote(),
        size.getStock()
    );
  }
}
