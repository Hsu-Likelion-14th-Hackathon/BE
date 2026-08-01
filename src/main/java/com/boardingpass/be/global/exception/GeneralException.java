package com.boardingpass.be.global.exception;

import com.boardingpass.be.global.apiPayload.code.BaseErrorCode;
import com.boardingpass.be.global.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
  private final BaseErrorCode code;

  public ErrorReasonDto getErrorReason() {
    return this.code.getReason();
  }

  public ErrorReasonDto getErrorReasonHttpStatus() {
    return this.code.getReasonHttpStatus();
  }
}
