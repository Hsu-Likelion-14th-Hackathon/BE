package com.boardingpass.be.domain.boardingpass.dto;

import com.boardingpass.be.domain.boardingpass.RouteStep;
import java.util.List;

public record BoardingPassRouteResponse(
    Long boardingPassId,
    List<RouteStepResponse> steps
) {
  public static BoardingPassRouteResponse of(Long boardingPassId, List<RouteStep> steps) {
    return new BoardingPassRouteResponse(
        boardingPassId,
        steps.stream().map(RouteStepResponse::from).toList()
    );
  }
}