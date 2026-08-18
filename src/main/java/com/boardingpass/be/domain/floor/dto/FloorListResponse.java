package com.boardingpass.be.domain.floor.dto;

import java.util.List;

public record FloorListResponse(
    String storeName,
    String introAudioUrl,
    String guideAudioUrl,
    List<FloorSummaryResponse> floors
) {
  public static FloorListResponse of(
      String storeName,
      String introAudioUrl,
      String guideAudioUrl,
      List<FloorSummaryResponse> floors
  ) {
    return new FloorListResponse(
        storeName,
        blankToNull(introAudioUrl),
        blankToNull(guideAudioUrl),
        floors
    );
  }

  private static String blankToNull(String value) {
    return (value == null || value.isBlank()) ? null : value;
  }
}