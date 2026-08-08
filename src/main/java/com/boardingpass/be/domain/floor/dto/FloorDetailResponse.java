package com.boardingpass.be.domain.floor.dto;

import com.boardingpass.be.domain.floor.Floor;
import java.util.Comparator;
import java.util.List;

public record FloorDetailResponse(
    Long floorId,
    Integer floorNo,
    String code,
    String title,
    String tagline,
    String audioUrl,
    List<FloorContentResponse> contents
) {
  public static FloorDetailResponse from(Floor floor) {
    List<FloorContentResponse> contents = floor.getContents().stream()
        .sorted(Comparator.comparing(c -> c.getOrderNo()))
        .map(FloorContentResponse::from)
        .toList();

    return new FloorDetailResponse(
        floor.getId(),
        floor.getFloorNo(),
        floor.getCode(),
        floor.getTitle(),
        floor.getTagline(),
        blankToNull(floor.getAudioUrl()),
        contents
    );
  }

  private static String blankToNull(String value) {
    return (value == null || value.isBlank()) ? null : value;
  }
}