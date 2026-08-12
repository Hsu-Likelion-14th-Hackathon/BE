package com.boardingpass.be.domain.boardingpass.dto;

import com.boardingpass.be.domain.boardingpass.BoardingPass;
import com.boardingpass.be.domain.boardingpass.BoardingPassStatus;
import java.time.LocalDateTime;

public record BoardingPassSummaryResponse(
    Long boardingPassId,
    String passCode,
    BoardingPassStatus status,
    String passengerName,
    LocalDateTime issuedAt
) {
  public static BoardingPassSummaryResponse from(BoardingPass pass) {
    return new BoardingPassSummaryResponse(
        pass.getId(),
        pass.getPassCode(),
        pass.getStatus(),
        pass.getUser().getName(),
        pass.getCreatedAt()
    );
  }
}