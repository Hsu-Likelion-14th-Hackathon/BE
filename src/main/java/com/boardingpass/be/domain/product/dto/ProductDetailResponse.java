package com.boardingpass.be.domain.product.dto;

import com.boardingpass.be.domain.product.Product;
import java.util.List;
import java.util.Set;

public record ProductDetailResponse(
    Long productId,
    String name,
    Integer price,
    String description,
    List<ProductColorResponse> colors
) {
  public static ProductDetailResponse from(Product product, Set<Long> wishedProductColorIds) {
    return new ProductDetailResponse(
        product.getId(),
        product.getName(),
        product.getPrice(),
        product.getDescription(),
        product.getColors().stream()
            .map(color -> ProductColorResponse.from(color, wishedProductColorIds))
            .toList()
    );
  }
}
