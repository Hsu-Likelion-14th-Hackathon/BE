package com.boardingpass.be.domain.product.dto;

import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductImage;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public record ProductSummaryResponse(
    Long productId,
    Long productColorId,
    String name,
    Integer price,
    String thumbnailImageUrl,
    List<ProductColorSummaryResponse> colors,
    boolean isWished
) {
  public static ProductSummaryResponse from(ProductColor color, Set<Long> wishedProductColorIds) {
    return new ProductSummaryResponse(
        color.getProduct().getId(),
        color.getId(),
        color.getProduct().getName(),
        color.getProduct().getPrice(),
        resolveThumbnailUrl(color),
        color.getProduct().getColors().stream().map(ProductColorSummaryResponse::from).toList(),
        wishedProductColorIds.contains(color.getId())
    );
  }

  private static String resolveThumbnailUrl(ProductColor color) {
    return color.getImages().stream()
        .min(Comparator.comparing(ProductImage::getOrderNo))
        .map(ProductImage::getImageUrl)
        .orElse(null);
  }
}
