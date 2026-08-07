package com.boardingpass.be.domain.fitting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadUrlResponse {
  private String uploadUrl;
  private String fileKey;
  private int expiresIn;
}