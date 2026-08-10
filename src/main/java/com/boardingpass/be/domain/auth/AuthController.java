package com.boardingpass.be.domain.auth;

import com.boardingpass.be.domain.auth.dto.KakaoLoginRequest;
import com.boardingpass.be.domain.auth.dto.KakaoLoginResponse;
import com.boardingpass.be.domain.auth.dto.ProfileRequest;
import com.boardingpass.be.domain.auth.dto.ProfileResponse;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "카카오 로그인")
  @PostMapping("/kakao")
  public ApiResponse<KakaoLoginResponse> loginWithKakao(@Valid @RequestBody KakaoLoginRequest request) {
    return ApiResponse.onSuccess(authService.loginWithKakao(request));
  }

  @Operation(summary = "추가 정보 입력")
  @PostMapping("/profile")
  public ApiResponse<ProfileResponse> completeProfile(@Valid @RequestBody ProfileRequest request) {
    return ApiResponse.onSuccess(authService.completeProfile(request));
  }
}
