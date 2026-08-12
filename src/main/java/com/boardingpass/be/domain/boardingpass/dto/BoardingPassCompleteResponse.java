package com.boardingpass.be.domain.boardingpass.dto;

import com.boardingpass.be.domain.boardingpass.BoardingPass;
import com.boardingpass.be.domain.boardingpass.BoardingPassStatus;
import com.boardingpass.be.domain.passport.Passport;
import com.boardingpass.be.domain.passport.PassportStamp;
import com.boardingpass.be.domain.store.VisitLog;

public record BoardingPassCompleteResponse(
    Long boardingPassId,
    BoardingPassStatus status,
    Integer stayMinutes,
    Long passportStampId,
    Integer totalVisitCount
) {
  public static BoardingPassCompleteResponse of(
      BoardingPass pass,
      VisitLog visitLog,
      PassportStamp stamp,
      Passport passport
  ) {
    return new BoardingPassCompleteResponse(
        pass.getId(),
        pass.getStatus(),
        visitLog.getStayMinutes(),
        stamp.getId(),
        passport.getTotalVisitCount()
    );
  }
}