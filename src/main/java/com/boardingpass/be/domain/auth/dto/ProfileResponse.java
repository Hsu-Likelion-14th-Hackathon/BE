package com.boardingpass.be.domain.auth.dto;

import com.boardingpass.be.domain.user.User;
import java.time.LocalDate;

public record ProfileResponse(
    Long userId,
    String name,
    LocalDate birthDate,
    String nationality
) {
  public static ProfileResponse from(User user) {
    return new ProfileResponse(
        user.getId(),
        user.getName(),
        user.getBirthDate(),
        user.getNationality()
    );
  }
}
