package com.boardingpass.be.domain.floor;

import com.boardingpass.be.domain.floor.dto.FloorDetailResponse;
import com.boardingpass.be.domain.floor.dto.FloorListResponse;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Floor", description = "층(Travel Guide) API")
@RestController
@RequestMapping("/floors")
@RequiredArgsConstructor
public class FloorController {

  private final FloorService floorService;

  @Operation(summary = "층 목록 조회")
  @GetMapping
  public ApiResponse<FloorListResponse> getFloors() {
    return ApiResponse.onSuccess(floorService.getFloors());
  }

  @Operation(summary = "층 상세 조회")
  @GetMapping("/{floorId}")
  public ApiResponse<FloorDetailResponse> getFloorDetail(@PathVariable Long floorId) {
    return ApiResponse.onSuccess(floorService.getFloorDetail(floorId));
  }
}