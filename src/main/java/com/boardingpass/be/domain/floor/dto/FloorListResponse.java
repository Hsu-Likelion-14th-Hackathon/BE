package com.boardingpass.be.domain.floor.dto;

import java.util.List;

public record FloorListResponse(
    String storeName,
    List<FloorSummaryResponse> floors
) {
  public static FloorListResponse of(String storeName, List<FloorSummaryResponse> floors) {
    return new FloorListResponse(storeName, floors);
  }
}