package com.boardingpass.be.domain.boardingpass.route;

import com.boardingpass.be.domain.floor.Floor;

public record RecommendedStep(
    Floor floor,
    int sequence,
    boolean recommended,
    String reason
) {
  public RecommendedStep withReason(String reason) {
    return new RecommendedStep(floor, sequence, recommended, reason);
  }
}