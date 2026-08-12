package com.boardingpass.be.domain.passport;

import com.boardingpass.be.domain.passport.dto.PassportCreditsResponse;
import com.boardingpass.be.domain.passport.dto.PassportResponse;
import com.boardingpass.be.domain.passport.dto.PassportStampsResponse;
import com.boardingpass.be.domain.passport.dto.PassportVisitDetailResponse;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Passport", description = "Passport API")
@RestController
@RequestMapping("/passport")
@RequiredArgsConstructor
public class PassportController {

  private final PassportService passportService;

  @Operation(summary = "여권 신분면 조회")
  @GetMapping
  public ApiResponse<PassportResponse> getPassport() {
    return ApiResponse.onSuccess(passportService.getPassport());
  }

  @Operation(summary = "방문 스탬프 목록 조회")
  @GetMapping("/stamps")
  public ApiResponse<PassportStampsResponse> getStamps() {
    return ApiResponse.onSuccess(passportService.getStamps());
  }

  @Operation(summary = "방문 상세 조회")
  @GetMapping("/visits/{visitLogId}")
  public ApiResponse<PassportVisitDetailResponse> getVisitDetail(
      @PathVariable Long visitLogId
  ) {
    return ApiResponse.onSuccess(passportService.getVisitDetail(visitLogId));
  }

  @Operation(summary = "크레딧 내역 조회")
  @GetMapping("/credits")
  public ApiResponse<PassportCreditsResponse> getCredits() {
    return ApiResponse.onSuccess(passportService.getCredits());
  }
}