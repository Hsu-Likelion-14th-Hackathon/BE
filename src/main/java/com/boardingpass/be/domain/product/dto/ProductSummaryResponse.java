package com.boardingpass.be.domain.product.dto;

import com.boardingpass.be.domain.product.Product;
import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductImage;
import java.util.Comparator;
import java.util.List;

public record ProductSummaryResponse(
    Long productId,
    String name,
    Integer price,
    String thumbnailImageUrl,
    List<ProductColorSummaryResponse> colors
) {
  public static ProductSummaryResponse from(Product product) {
    return new ProductSummaryResponse(
        product.getId(),
        product.getName(),
        product.getPrice(),
        resolveThumbnailUrl(product),
        product.getColors().stream().map(ProductColorSummaryResponse::from).toList()
    );
  }

  private static String resolveThumbnailUrl(Product product) {
    return product.getColors().stream()
        .filter(ProductColor::isDefault)
        .findFirst()
        .flatMap(color -> color.getImages().stream()
            .min(Comparator.comparing(ProductImage::getOrderNo)))
        .map(ProductImage::getImageUrl)
        .orElse(null);
  }
}
