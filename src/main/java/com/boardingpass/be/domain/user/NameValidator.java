package com.boardingpass.be.domain.user;

import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import java.util.regex.Pattern;

public final class NameValidator {

  private static final Pattern ENGLISH_NAME = Pattern.compile("^[A-Z '-]+$");

  private NameValidator() {
  }

  public static void validate(String name) {
    if (name == null || !ENGLISH_NAME.matcher(name).matches()) {
      throw new GeneralException(ErrorStatus.INVALID_NAME);
    }
  }
}
