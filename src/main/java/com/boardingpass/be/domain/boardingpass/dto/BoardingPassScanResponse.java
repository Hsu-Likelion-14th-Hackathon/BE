package com.boardingpass.be.domain.boardingpass.dto;

import com.boardingpass.be.domain.boardingpass.BoardingPass;
import com.boardingpass.be.domain.boardingpass.BoardingPassStatus;
import com.boardingpass.be.domain.store.VisitLog;
import java.time.LocalDateTime;

public record BoardingPassScanResponse(
    Long boardingPassId,
    BoardingPassStatus status,
    Long visitLogId,
    String entryNo,
    LocalDateTime scannedAt,
    Integer earnedCredit,
    Integer creditBalance
) {
  public static BoardingPassScanResponse of(
      BoardingPass pass,
      VisitLog visitLog,
      int earnedCredit,
      int creditBalance
  ) {
    return new BoardingPassScanResponse(
        pass.getId(),
        pass.getStatus(),
        visitLog.getId(),
        visitLog.getEntryNo(),
        visitLog.getScannedAt(),
        earnedCredit,
        creditBalance
    );
  }
}