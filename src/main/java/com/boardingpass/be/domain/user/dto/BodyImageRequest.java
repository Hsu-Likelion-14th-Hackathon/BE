package com.boardingpass.be.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BodyImageRequest {

  @NotBlank
  private String fileKey;
}