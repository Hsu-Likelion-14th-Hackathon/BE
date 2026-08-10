package com.boardingpass.be.domain.user;

import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import java.util.regex.Pattern;

/**
 * 프론트가 전달하는 국적 코드(ISO 3166-1 alpha-2, 예: KR/US/DE)의 형식만 검증합니다.
 * 별도 매핑 없이 그대로 저장하는 정책이므로 값 자체의 유효성(실존 국가 여부)은 검사하지 않습니다.
 */
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
