package com.boardingpass.be.domain.user.dto;

import com.boardingpass.be.domain.user.User;
import java.time.LocalDate;

public record UserUpdateResponse(
    Long userId,
    String name,
    LocalDate birthDate,
    String nationality
) {
  public static UserUpdateResponse from(User user) {
    return new UserUpdateResponse(
        user.getId(),
        user.getName(),
        user.getBirthDate(),
        user.getNationality()
    );
  }
}
