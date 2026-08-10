package com.boardingpass.be.domain.auth.kakao;

/**
 * 카카오 사용자 조회 결과를 도메인에서 쓰기 좋은 형태로 옮겨 담은 값 객체.
 */
public record KakaoUserInfo(String providerUid, String email) {
}
