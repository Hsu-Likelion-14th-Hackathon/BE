package com.boardingpass.be.domain.passport.dto;

import com.boardingpass.be.domain.passport.Passport;
import com.boardingpass.be.domain.user.User;
import java.time.LocalDate;

public record PassportResponse(
    String passportNo,
    String name,
    String nationality,
    LocalDate issuedOn,
    Integer creditBalance,
    Integer totalVisitCount
) {
  public static PassportResponse from(Passport passport) {
    User user = passport.getUser();
    return new PassportResponse(
        passport.getPassportNo(),
        user.getName(),
        user.getNationality(),
        passport.getCreatedAt().toLocalDate(),
        passport.getCreditBalance(),
        passport.getTotalVisitCount()
    );
  }
}