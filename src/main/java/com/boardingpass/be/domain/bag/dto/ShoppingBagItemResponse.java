package com.boardingpass.be.domain.bag.dto;

import com.boardingpass.be.domain.bag.entity.ShoppingBagItem;
import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductImage;
import com.boardingpass.be.domain.product.ProductSize;
import java.util.Comparator;

public record ShoppingBagItemResponse(
    Long shoppingBagItemId,
    Long productSizeId,
    Long productId,
    String name,
    Integer price,
    String thumbnailImageUrl,
    String colorName,
    String sizeLabel,
    String sizeNote,
    String sku,
    Integer quantity
) {
  public static ShoppingBagItemResponse from(ShoppingBagItem item) {
    ProductSize size = item.getProductSize();
    ProductColor color = size.getProductColor();

    return new ShoppingBagItemResponse(
        item.getId(),
        size.getId(),
        color.getProduct().getId(),
        color.getProduct().getName(),
        color.getProduct().getPrice(),
        resolveThumbnailUrl(color),
        color.getColorName(),
        size.getSizeLabel(),
        size.getSizeNote(),
        size.getSku(),
        item.getQuantity()
    );
  }

  private static String resolveThumbnailUrl(ProductColor color) {
    return color.getImages().stream()
        .min(Comparator.comparing(ProductImage::getOrderNo))
        .map(ProductImage::getImageUrl)
        .orElse(null);
  }
}
