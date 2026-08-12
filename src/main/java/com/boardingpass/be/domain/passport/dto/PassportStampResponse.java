package com.boardingpass.be.domain.passport.dto;

import com.boardingpass.be.domain.passport.PassportStamp;
import java.time.LocalDate;

public record PassportStampResponse(
    Long passportStampId,
    Long visitLogId,
    LocalDate stampedOn,
    String stampAssetUrl
) {
  public static PassportStampResponse from(PassportStamp stamp) {
    return new PassportStampResponse(
        stamp.getId(),
        stamp.getVisitLog().getId(),
        stamp.getCreatedAt().toLocalDate(),
        stamp.getStampAssetUrl()
    );
  }
}