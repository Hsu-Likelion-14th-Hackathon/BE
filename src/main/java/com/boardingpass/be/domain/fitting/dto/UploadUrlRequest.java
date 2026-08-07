package com.boardingpass.be.domain.fitting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UploadUrlRequest {

  @NotBlank
  private String fileName;

  @NotBlank
  private String contentType;
}