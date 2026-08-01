package com.boardingpass.be.global.exception.handler;

import com.boardingpass.be.global.apiPayload.code.BaseErrorCode;
import com.boardingpass.be.global.exception.GeneralException;

public class GlobalHandler extends GeneralException {

  public GlobalHandler(BaseErrorCode code) {
    super(code);
  }
}
