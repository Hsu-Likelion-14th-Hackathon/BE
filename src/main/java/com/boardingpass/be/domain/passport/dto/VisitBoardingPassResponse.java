package com.boardingpass.be.domain.passport.dto;

import com.boardingpass.be.domain.boardingpass.BoardingPass;

public record VisitBoardingPassResponse(
    Long boardingPassId,
    String passCode,
    String passengerName
) {
  public static VisitBoardingPassResponse from(BoardingPass boardingPass) {
    return new VisitBoardingPassResponse(
        boardingPass.getId(),
        boardingPass.getPassCode(),
        boardingPass.getUser().getName()
    );
  }
}