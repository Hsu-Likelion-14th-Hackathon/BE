package com.boardingpass.be.domain.boardingpass.dto;

import com.boardingpass.be.domain.boardingpass.RouteStep;
import com.boardingpass.be.domain.floor.Floor;

public record RouteStepResponse(
    Integer sequence,
    Long floorId,
    Integer floorNo,
    String code,
    String title,
    String subtitle,
    String tagline,
    Boolean isRecommended,
    String reason
) {
  public static RouteStepResponse from(RouteStep step) {
    Floor floor = step.getFloor();
    return new RouteStepResponse(
        step.getSequence(),
        floor.getId(),
        floor.getFloorNo(),
        floor.getCode(),
        floor.getTitle(),
        floor.getSubtitle(),
        floor.getTagline(),
        step.getIsRecommended(),
        step.getReason()
    );
  }
}