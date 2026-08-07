package com.boardingpass.be.domain.user;

import com.boardingpass.be.domain.user.dto.BodyImageDeleteResponse;
import com.boardingpass.be.domain.user.dto.BodyImageRequest;
import com.boardingpass.be.domain.user.dto.BodyImageResponse;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 API")
@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserBodyImageController {

  private final UserBodyImageService userBodyImageService;

  @Operation(summary = "바디 이미지 등록/수정")
  @PutMapping("/body-image")
  public ApiResponse<BodyImageResponse> updateBodyImage(@Valid @RequestBody BodyImageRequest request) {
    return ApiResponse.onSuccess(userBodyImageService.updateBodyImage(request.getFileKey()));
  }

  @Operation(summary = "바디 이미지 삭제")
  @DeleteMapping("/body-image")
  public ApiResponse<BodyImageDeleteResponse> deleteBodyImage() {
    return ApiResponse.onSuccess(userBodyImageService.deleteBodyImage());
  }
}