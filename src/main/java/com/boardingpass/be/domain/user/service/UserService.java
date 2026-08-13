package com.boardingpass.be.domain.user.service;

import com.boardingpass.be.domain.user.NameValidator;
import com.boardingpass.be.domain.user.NationalityValidator;
import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.domain.user.dto.UserMeResponse;
import com.boardingpass.be.domain.user.dto.UserUpdateRequest;
import com.boardingpass.be.domain.user.dto.UserUpdateResponse;
import com.boardingpass.be.domain.user.repository.UserRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public UserMeResponse getMe() {
    User user = findCurrentUser();
    return UserMeResponse.from(user);
  }

  @Transactional
  public UserUpdateResponse updateMe(UserUpdateRequest request) {
    User user = findCurrentUser();

    NameValidator.validate(request.name());
    NationalityValidator.validate(request.nationality());
    user.completeProfile(request.name(), request.birthDate(), request.nationality());

    return UserUpdateResponse.from(user);
  }

  private User findCurrentUser() {
    Long userId = SecurityUtils.getCurrentUserId();
    return userRepository.findById(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
  }
}
