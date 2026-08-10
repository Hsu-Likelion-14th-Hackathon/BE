package com.boardingpass.be.domain.auth.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://kauth.kakao.com/oauth/token 응답 매핑용 DTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoTokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType
) {
}
