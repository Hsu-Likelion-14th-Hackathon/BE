package com.boardingpass.be.domain.auth.kakao;

import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 카카오 인가코드를 액세스 토큰으로 교환하고, 그 토큰으로 사용자 정보를 조회합니다.
 * redirect_uri는 인가 코드를 발급받을 때 프론트가 사용한 값과 동일해야 하므로 요청에서 그대로 전달받습니다.
 */
@Component
public class KakaoAuthClient {

  private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
  private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

  private final RestTemplate restTemplate;

  @Value("${kakao.client-id}")
  private String clientId;

  public KakaoAuthClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public KakaoUserInfo authenticate(String code, String redirectUri) {
    String kakaoAccessToken = requestAccessToken(code, redirectUri);
    return requestUserInfo(kakaoAccessToken);
  }

  private String requestAccessToken(String code, String redirectUri) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", clientId);
    body.add("redirect_uri", redirectUri);
    body.add("code", code);

    try {
      ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
          TOKEN_URI, new HttpEntity<>(body, headers), KakaoTokenResponse.class);

      KakaoTokenResponse tokenResponse = response.getBody();
      if (tokenResponse == null || tokenResponse.accessToken() == null) {
        throw new GeneralException(ErrorStatus.KAKAO_AUTH_FAILED);
      }
      return tokenResponse.accessToken();
    } catch (RestClientException e) {
      throw new GeneralException(ErrorStatus.KAKAO_AUTH_FAILED);
    }
  }

  private KakaoUserInfo requestUserInfo(String kakaoAccessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(kakaoAccessToken);

    try {
      ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
          USER_INFO_URI, HttpMethod.GET, new HttpEntity<>(headers), KakaoUserInfoResponse.class);

      KakaoUserInfoResponse userInfo = response.getBody();
      if (userInfo == null || userInfo.id() == null) {
        throw new GeneralException(ErrorStatus.KAKAO_AUTH_FAILED);
      }

      String email = userInfo.kakaoAccount() != null ? userInfo.kakaoAccount().email() : null;
      return new KakaoUserInfo(String.valueOf(userInfo.id()), email);
    } catch (RestClientException e) {
      throw new GeneralException(ErrorStatus.KAKAO_AUTH_FAILED);
    }
  }
}
