package com.boardingpass.be.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record ProfileRequest(
    @NotBlank String name,
    @NotNull @Past LocalDate birthDate,
    @NotBlank String nationality
) {
}
