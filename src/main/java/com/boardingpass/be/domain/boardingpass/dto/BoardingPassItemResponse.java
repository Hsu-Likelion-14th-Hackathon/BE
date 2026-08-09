package com.boardingpass.be.domain.boardingpass.dto;

import com.boardingpass.be.domain.boardingpass.BoardingPassItem;
import com.boardingpass.be.domain.boardingpass.ItemSource;

public record BoardingPassItemResponse(
    Long productColorId,
    String name,
    ItemSource source
) {
  public static BoardingPassItemResponse from(BoardingPassItem item) {
    return new BoardingPassItemResponse(
        item.getProductColor().getId(),
        item.getProductColor().getProduct().getName(),
        item.getSource()
    );
  }
}