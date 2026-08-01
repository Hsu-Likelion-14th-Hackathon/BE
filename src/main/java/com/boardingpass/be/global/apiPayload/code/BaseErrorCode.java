package com.boardingpass.be.global.apiPayload.code;

public interface BaseErrorCode {
  ErrorReasonDto getReason();

  ErrorReasonDto getReasonHttpStatus();
}
