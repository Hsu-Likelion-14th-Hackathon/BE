package com.boardingpass.be.domain.floor.dto;

import com.boardingpass.be.domain.floor.BlockType;
import com.boardingpass.be.domain.floor.FloorContent;

public record FloorContentResponse(
    Integer orderNo,
    BlockType blockType,
    String body,
    String imageUrl,
    FloorProductResponse product
) {
  public static FloorContentResponse from(FloorContent content) {
    FloorProductResponse product = content.getBlockType() == BlockType.PRODUCT
        ? FloorProductResponse.from(content.getProduct())
        : null;

    return new FloorContentResponse(
        content.getOrderNo(),
        content.getBlockType(),
        content.getBody(),
        blankToNull(content.getImageUrl()),
        product
    );
  }

  private static String blankToNull(String value) {
    return (value == null || value.isBlank()) ? null : value;
  }
}
