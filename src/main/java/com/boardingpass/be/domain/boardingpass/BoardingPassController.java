package com.boardingpass.be.domain.boardingpass;

import com.boardingpass.be.domain.boardingpass.dto.BoardingPassCompleteResponse;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassIssueRequest;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassIssueResponse;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassRouteResponse;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassScanRequest;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassScanResponse;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassSummaryResponse;
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

@Tag(name = "Boarding Pass", description = "Boarding Pass API")
@RestController
@RequestMapping("/boarding-passes")
@RequiredArgsConstructor
public class BoardingPassController {

  private final BoardingPassService boardingPassService;

  @Operation(summary = "Boarding Pass 발급")
  @PostMapping
  public ApiResponse<BoardingPassIssueResponse> issue(
      @Valid @RequestBody BoardingPassIssueRequest request
  ) {
    return ApiResponse.onSuccess(boardingPassService.issue(request));
  }

  @Operation(summary = "최근 Boarding Pass 조회")
  @GetMapping("/latest")
  public ApiResponse<BoardingPassSummaryResponse> getLatest() {
    return ApiResponse.onSuccess(boardingPassService.getLatest());
  }

  @Operation(summary = "Boarding Pass 스캔")
  @PostMapping("/{boardingPassId}/scan")
  public ApiResponse<BoardingPassScanResponse> scan(
      @PathVariable Long boardingPassId,
      @RequestBody(required = false) BoardingPassScanRequest request
  ) {
    return ApiResponse.onSuccess(boardingPassService.scan(boardingPassId, request));
  }

  @Operation(summary = "AI 추천 동선 조회")
  @GetMapping("/{boardingPassId}/route")
  public ApiResponse<BoardingPassRouteResponse> getRoute(@PathVariable Long boardingPassId) {
    return ApiResponse.onSuccess(boardingPassService.getRoute(boardingPassId));
  }

  @Operation(summary = "비행 종료")
  @PostMapping("/{boardingPassId}/complete")
  public ApiResponse<BoardingPassCompleteResponse> complete(@PathVariable Long boardingPassId) {
    return ApiResponse.onSuccess(boardingPassService.complete(boardingPassId));
  }
}