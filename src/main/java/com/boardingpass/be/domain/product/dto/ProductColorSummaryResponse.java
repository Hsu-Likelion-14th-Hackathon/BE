package com.boardingpass.be.domain.product.dto;

import com.boardingpass.be.domain.product.ProductColor;

public record ProductColorSummaryResponse(
    Long productColorId,
    String colorName,
    String colorHex,
    boolean isDefault
) {
  public static ProductColorSummaryResponse from(ProductColor color) {
    return new ProductColorSummaryResponse(
        color.getId(),
        color.getColorName(),
        color.getColorHex(),
        color.isDefault()
    );
  }
}
