package com.boardingpass.be.domain.health.controller;

import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "서버 상태 확인")
@RestController
@RequestMapping("/api/health")
public class HealthController {

  @Operation(summary = "헬스 체크")
  @GetMapping
  public ApiResponse<Map<String, String>> health() {
    return ApiResponse.onSuccess(Map.of(
        "status", "UP",
        "service", "boardingpass"
    ));
  }
}
