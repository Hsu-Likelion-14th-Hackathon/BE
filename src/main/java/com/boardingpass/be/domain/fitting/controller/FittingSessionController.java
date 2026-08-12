package com.boardingpass.be.domain.fitting.controller;

import com.boardingpass.be.domain.fitting.dto.FittingSessionCreateRequest;
import com.boardingpass.be.domain.fitting.dto.FittingSessionResponse;
import com.boardingpass.be.domain.fitting.service.FittingSessionService;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Fitting", description = "AI 피팅 API")
@RestController
@RequestMapping("/fitting-sessions")
@RequiredArgsConstructor
public class FittingSessionController {

  private final FittingSessionService fittingSessionService;

  @Operation(summary = "AI 피팅 요청")
  @PostMapping
  public ApiResponse<FittingSessionResponse> createFittingSession(
      @Valid @RequestBody FittingSessionCreateRequest request
  ) {
    return ApiResponse.onSuccess(fittingSessionService.createFittingSession(request));
  }

  @Operation(summary = "AI 피팅 결과 조회")
  @GetMapping("/{fittingSessionId}")
  public ApiResponse<FittingSessionResponse> getFittingSession(@PathVariable Long fittingSessionId) {
    return ApiResponse.onSuccess(fittingSessionService.getFittingSession(fittingSessionId));
  }
}
