package com.boardingpass.be.domain.wishlist.dto;

import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductImage;
import com.boardingpass.be.domain.wishlist.Wishlist;
import java.util.Comparator;

public record WishlistItemResponse(
    Long productColorId,
    Long productId,
    String name,
    Integer price,
    String thumbnailImageUrl,
    String colorName
) {
  public static WishlistItemResponse from(Wishlist wishlist) {
    return from(wishlist.getProductColor());
  }

  public static WishlistItemResponse from(ProductColor color) {
    return new WishlistItemResponse(
        color.getId(),
        color.getProduct().getId(),
        color.getProduct().getName(),
        color.getProduct().getPrice(),
        resolveThumbnailUrl(color),
        color.getColorName()
    );
  }

  private static String resolveThumbnailUrl(ProductColor color) {
    return color.getImages().stream()
        .min(Comparator.comparing(ProductImage::getOrderNo))
        .map(ProductImage::getImageUrl)
        .orElse(null);
  }
}
