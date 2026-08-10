package com.boardingpass.be.global.apiPayload.code.status;

import com.boardingpass.be.global.apiPayload.code.BaseErrorCode;
import com.boardingpass.be.global.apiPayload.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
  _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
  _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
  _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
  _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

  UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE4002", "지원하지 않는 이미지 형식입니다."),
  INVALID_FILE_KEY(HttpStatus.BAD_REQUEST, "FILE4003", "유효하지 않은 파일 키입니다."),
  BODY_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "BODY_IMAGE_NOT_FOUND", "저장된 바디 이미지가 없습니다."),
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE5001", "파일 업로드 URL 발급에 실패했습니다."),

  STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_NOT_FOUND", "매장 정보를 찾을 수 없습니다."),
  FLOOR_NOT_FOUND(HttpStatus.NOT_FOUND, "FLOOR_NOT_FOUND", "존재하지 않는 층입니다."),

  SURVEY_INCOMPLETE(HttpStatus.BAD_REQUEST, "SURVEY_INCOMPLETE", "필수 설문 문항에 모두 응답해야 합니다."),
  INVALID_SURVEY_ANSWER(HttpStatus.BAD_REQUEST, "INVALID_SURVEY_ANSWER", "설문 응답이 올바르지 않습니다."),

  KAKAO_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "KAKAO_AUTH_FAILED", "카카오 인증에 실패했습니다."),
  PROFILE_ALREADY_REGISTERED(HttpStatus.CONFLICT, "PROFILE_ALREADY_REGISTERED", "이미 추가 정보가 등록된 회원입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 회원입니다."),
  INVALID_NATIONALITY(HttpStatus.BAD_REQUEST, "INVALID_NATIONALITY", "국적은 ISO 3166-1 alpha-2 형식의 2자리 코드여야 합니다."),

  MALFORMED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4101", "잘못 구성된 JWT 형식입니다."),
  UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4102", "지원하지 않는 JWT 형식입니다."),
  EMPTY_JWT_CLAIMS(HttpStatus.BAD_REQUEST, "AUTH4103", "JWT 클레임이 비어 있습니다."),
  INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH4111", "유효하지 않은 JWT 서명입니다."),
  EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4112", "JWT 토큰이 만료되었습니다."),
  INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "AUTH4114", "유효하지 않은 토큰 타입입니다."),
  MISSING_JWT(HttpStatus.UNAUTHORIZED, "AUTH4115", "JWT 토큰이 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ErrorReasonDto getReason() {
    return ErrorReasonDto.builder()
        .isSuccess(false)
        .message(message)
        .code(code)
        .build();
  }

  @Override
  public ErrorReasonDto getReasonHttpStatus() {
    return ErrorReasonDto.builder()
        .httpStatus(httpStatus)
        .isSuccess(false)
        .code(code)
        .message(message)
        .build();
  }
}
