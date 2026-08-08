package com.boardingpass.be.domain.floor.dto;

import com.boardingpass.be.domain.product.Product;
import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductImage;
import java.util.Comparator;

public record FloorProductResponse(
    Long productId,
    String name,
    Integer price,
    String imageUrl
) {
  public static FloorProductResponse from(Product product) {
    if (product == null) {
      return null;
    }
    return new FloorProductResponse(
        product.getId(),
        product.getName(),
        product.getPrice(),
        resolveDefaultImageUrl(product)
    );
  }

  private static String resolveDefaultImageUrl(Product product) {
    return product.getColors().stream()
        .filter(ProductColor::isDefault)
        .findFirst()
        .flatMap(color -> color.getImages().stream()
            .min(Comparator.comparing(ProductImage::getOrderNo)))
        .map(ProductImage::getImageUrl)
        .orElse(null);
  }
}