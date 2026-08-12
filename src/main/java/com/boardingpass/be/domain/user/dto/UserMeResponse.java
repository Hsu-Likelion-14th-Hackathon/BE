package com.boardingpass.be.domain.user.dto;

import com.boardingpass.be.domain.user.Provider;
import com.boardingpass.be.domain.user.User;
import java.time.LocalDate;

public record UserMeResponse(
    Long userId,
    String name,
    String email,
    Provider provider,
    String nationality,
    LocalDate birthDate,
    String defaultBodyImageUrl,
    String passportNo
) {
  public static UserMeResponse of(User user, String passportNo) {
    return new UserMeResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getProvider(),
        user.getNationality(),
        user.getBirthDate(),
        user.getDefaultBodyImageUrl(),
        passportNo
    );
  }
}
