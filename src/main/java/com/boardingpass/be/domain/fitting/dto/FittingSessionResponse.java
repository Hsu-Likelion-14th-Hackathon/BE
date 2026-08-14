package com.boardingpass.be.domain.fitting.dto;

import com.boardingpass.be.domain.fitting.FittingStatus;
import com.boardingpass.be.domain.fitting.entity.FittingSession;
import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductImage;
import java.util.Comparator;

public record FittingSessionResponse(
    Long fittingSessionId,
    FittingStatus status,
    String resultImageUrl,
    Integer creditCost,
    Long productColorId,
    Long productId,
    String name,
    Integer price,
    String thumbnailImageUrl
) {
  public static FittingSessionResponse from(FittingSession session) {
    ProductColor color = session.getProductColor();

    return new FittingSessionResponse(
        session.getId(),
        session.getStatus(),
        session.getResultImageUrl(),
        session.getCreditCost(),
        color.getId(),
        color.getProduct().getId(),
        color.getProduct().getName(),
        color.getProduct().getPrice(),
        resolveThumbnailUrl(color)
    );
  }

  private static String resolveThumbnailUrl(ProductColor color) {
    return color.getImages().stream()
        .min(Comparator.comparing(ProductImage::getOrderNo))
        .map(ProductImage::getImageUrl)
        .orElse(null);
  }
}
