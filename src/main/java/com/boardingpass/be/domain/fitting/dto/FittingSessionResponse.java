package com.boardingpass.be.domain.fitting.dto;

import com.boardingpass.be.domain.fitting.FittingStatus;
import com.boardingpass.be.domain.fitting.entity.FittingSession;

public record FittingSessionResponse(
    Long fittingSessionId,
    FittingStatus status,
    String resultImageUrl,
    Integer creditCost
) {
  public static FittingSessionResponse from(FittingSession session) {
    return new FittingSessionResponse(
        session.getId(),
        session.getStatus(),
        session.getResultImageUrl(),
        session.getCreditCost()
    );
  }
}
