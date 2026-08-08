package com.boardingpass.be.domain.floor.dto;

import com.boardingpass.be.domain.floor.Floor;

public record FloorSummaryResponse(
    Long floorId,
    Integer floorNo,
    String code,
    String title,
    String tagline,
    String audioUrl
) {
  public static FloorSummaryResponse from(Floor floor) {
    return new FloorSummaryResponse(
        floor.getId(),
        floor.getFloorNo(),
        floor.getCode(),
        floor.getTitle(),
        floor.getTagline(),
        blankToNull(floor.getAudioUrl())
    );
  }

  private static String blankToNull(String value) {
    return (value == null || value.isBlank()) ? null : value;
  }
}