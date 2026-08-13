package com.boardingpass.be.domain.product.dto;

import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductImage;
import java.util.List;
import java.util.Set;

public record ProductColorResponse(
    Long productColorId,
    String colorName,
    String colorHex,
    boolean isDefault,
    List<String> images,
    List<ProductSizeResponse> sizes,
    boolean isWished
) {
  public static ProductColorResponse from(ProductColor color, Set<Long> wishedProductColorIds) {
    return new ProductColorResponse(
        color.getId(),
        color.getColorName(),
        color.getColorHex(),
        color.isDefault(),
        color.getImages().stream().map(ProductImage::getImageUrl).toList(),
        color.getSizes().stream().map(ProductSizeResponse::from).toList(),
        wishedProductColorIds.contains(color.getId())
    );
  }
}
