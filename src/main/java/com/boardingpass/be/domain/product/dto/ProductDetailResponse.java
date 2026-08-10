package com.boardingpass.be.domain.product.dto;

import com.boardingpass.be.domain.product.Product;
import java.util.List;

public record ProductDetailResponse(
    Long productId,
    String name,
    Integer price,
    String description,
    List<ProductColorResponse> colors
) {
  public static ProductDetailResponse from(Product product) {
    return new ProductDetailResponse(
        product.getId(),
        product.getName(),
        product.getPrice(),
        product.getDescription(),
        product.getColors().stream().map(ProductColorResponse::from).toList()
    );
  }
}
