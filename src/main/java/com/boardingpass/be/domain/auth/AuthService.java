package com.boardingpass.be.domain.auth;

import com.boardingpass.be.domain.auth.dto.KakaoLoginRequest;
import com.boardingpass.be.domain.auth.dto.KakaoLoginResponse;
import com.boardingpass.be.domain.auth.dto.ProfileRequest;
import com.boardingpass.be.domain.auth.dto.ProfileResponse;
import com.boardingpass.be.domain.auth.kakao.KakaoAuthClient;
import com.boardingpass.be.domain.auth.kakao.KakaoUserInfo;
import com.boardingpass.be.domain.user.NationalityValidator;
import com.boardingpass.be.domain.user.Provider;
import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.domain.user.UserRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.jwt.JwtProvider;
import com.boardingpass.be.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final KakaoAuthClient kakaoAuthClient;
  private final JwtProvider jwtProvider;

  @Transactional
  public KakaoLoginResponse loginWithKakao(KakaoLoginRequest request) {
    KakaoUserInfo kakaoUserInfo = kakaoAuthClient.authenticate(request.code(), request.redirectUri());

    User user = userRepository.findByProviderAndProviderUid(Provider.KAKAO, kakaoUserInfo.providerUid())
        .orElse(null);

    boolean isNewUser = user == null;
    if (isNewUser) {
      user = userRepository.save(
          User.builder()
              .provider(Provider.KAKAO)
              .providerUid(kakaoUserInfo.providerUid())
              .email(kakaoUserInfo.email())
              .build()
      );
    }

    String accessToken = jwtProvider.generateAccessToken(user.getId());
    return new KakaoLoginResponse(accessToken, isNewUser, user.getId());
  }

  @Transactional
  public ProfileResponse completeProfile(ProfileRequest request) {
    Long userId = SecurityUtils.getCurrentUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

    if (user.getName() != null) {
      throw new GeneralException(ErrorStatus.PROFILE_ALREADY_REGISTERED);
    }

    NationalityValidator.validate(request.nationality());
    user.completeProfile(request.name(), request.birthDate(), request.nationality());

    return ProfileResponse.from(user);
  }
}
