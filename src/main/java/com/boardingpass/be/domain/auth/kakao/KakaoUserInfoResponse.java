package com.boardingpass.be.domain.auth.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://kapi.kakao.com/v2/user/me 응답 매핑용 DTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoResponse(
    Long id,
    @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record KakaoAccount(String email) {
  }
}
