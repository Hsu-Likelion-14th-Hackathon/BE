package com.boardingpass.be.domain.user;

import com.boardingpass.be.domain.user.dto.UserMeResponse;
import com.boardingpass.be.domain.user.dto.UserUpdateRequest;
import com.boardingpass.be.domain.user.dto.UserUpdateResponse;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 API")
@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(summary = "내 정보 조회")
  @GetMapping
  public ApiResponse<UserMeResponse> getMe() {
    return ApiResponse.onSuccess(userService.getMe());
  }

  @Operation(summary = "회원정보 수정")
  @PatchMapping
  public ApiResponse<UserUpdateResponse> updateMe(@Valid @RequestBody UserUpdateRequest request) {
    return ApiResponse.onSuccess(userService.updateMe(request));
  }
}
