package com.boardingpass.be.domain.fitting.controller;

import com.boardingpass.be.domain.fitting.dto.UploadUrlRequest;
import com.boardingpass.be.domain.fitting.dto.UploadUrlResponse;
import com.boardingpass.be.domain.storage.AzureBlobStorageService;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Fitting", description = "AI 피팅 API")
@RestController
@RequestMapping("/fitting-sessions")
@RequiredArgsConstructor
public class FittingUploadController {

  private final AzureBlobStorageService azureBlobStorageService;

  @Operation(summary = "업로드 URL 발급")
  @PostMapping("/upload-url")
  public ApiResponse<UploadUrlResponse> createUploadUrl(@Valid @RequestBody UploadUrlRequest request) {
    var result = azureBlobStorageService.createUploadUrl(request.getFileName(), request.getContentType());
    return ApiResponse.onSuccess(
        new UploadUrlResponse(result.uploadUrl(), result.fileKey(), result.expiresIn()));
  }
}