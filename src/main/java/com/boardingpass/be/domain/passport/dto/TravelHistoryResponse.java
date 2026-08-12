package com.boardingpass.be.domain.passport.dto;

import com.boardingpass.be.domain.boardingpass.RouteStep;
import com.boardingpass.be.domain.floor.Floor;

public record TravelHistoryResponse(
    Integer sequence,
    Long floorId,
    Integer floorNo,
    String code,
    String title,
    String tagline
) {
  public static TravelHistoryResponse from(RouteStep step) {
    Floor floor = step.getFloor();
    return new TravelHistoryResponse(
        step.getSequence(),
        floor.getId(),
        floor.getFloorNo(),
        floor.getCode(),
        floor.getTitle(),
        floor.getTagline()
    );
  }
}