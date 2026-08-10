package com.boardingpass.be.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record UserUpdateRequest(
    @NotBlank String name,
    @NotNull @Past LocalDate birthDate,
    @NotBlank String nationality
) {
}
