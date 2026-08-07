package com.boardingpass.be.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BodyImageDeleteResponse {
  private boolean deleted;
}