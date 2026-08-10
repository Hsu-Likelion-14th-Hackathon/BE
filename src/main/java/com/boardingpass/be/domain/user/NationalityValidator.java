package com.boardingpass.be.domain.user;

import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import java.util.regex.Pattern;

public final class NationalityValidator {

  private static final Pattern ISO_ALPHA2 = Pattern.compile("^[A-Z]{2}$");

  private NationalityValidator() {
  }

  public static void validate(String nationality) {
    if (nationality == null || !ISO_ALPHA2.matcher(nationality).matches()) {
      throw new GeneralException(ErrorStatus.INVALID_NATIONALITY);
    }
  }
}
