package com.boardingpass.be.domain.auth.dto;

import com.boardingpass.be.domain.passport.Passport;
import com.boardingpass.be.domain.user.User;
import java.time.LocalDate;

public record ProfileResponse(
    Long userId,
    String name,
    LocalDate birthDate,
    String nationality,
    String passportNo
) {
  public static ProfileResponse of(User user, Passport passport) {
    return new ProfileResponse(
        user.getId(),
        user.getName(),
        user.getBirthDate(),
        user.getNationality(),
        passport.getPassportNo()
    );
  }
}
