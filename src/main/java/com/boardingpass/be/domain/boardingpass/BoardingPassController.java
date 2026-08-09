package com.boardingpass.be.domain.boardingpass;

import com.boardingpass.be.domain.boardingpass.dto.BoardingPassIssueRequest;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassIssueResponse;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}