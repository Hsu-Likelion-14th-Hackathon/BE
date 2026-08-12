package com.boardingpass.be.domain.passport.dto;

import com.boardingpass.be.domain.passport.Passport;
import com.boardingpass.be.domain.passport.PassportStamp;
import java.util.List;

public record PassportStampsResponse(
    Integer totalVisitCount,
    List<PassportStampResponse> stamps
) {
  public static PassportStampsResponse of(Passport passport, List<PassportStamp> stamps) {
    return new PassportStampsResponse(
        passport.getTotalVisitCount(),
        stamps.stream().map(PassportStampResponse::from).toList()
    );
  }
}