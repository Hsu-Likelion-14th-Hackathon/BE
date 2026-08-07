package com.boardingpass.be.global.security;

import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

  private SecurityUtils() {}

  public static Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()
        || authentication.getPrincipal() == null
        || "anonymousUser".equals(authentication.getPrincipal())) {
      throw new GeneralException(ErrorStatus._UNAUTHORIZED);
    }
    try {
      return Long.parseLong(authentication.getName());
    } catch (NumberFormatException e) {
      throw new GeneralException(ErrorStatus._UNAUTHORIZED);
    }
  }
}