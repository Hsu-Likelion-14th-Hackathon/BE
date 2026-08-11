package com.boardingpass.be.domain.wishlist.controller;

import com.boardingpass.be.domain.wishlist.dto.WishlistCreateRequest;
import com.boardingpass.be.domain.wishlist.dto.WishlistDeleteResponse;
import com.boardingpass.be.domain.wishlist.dto.WishlistItemResponse;
import com.boardingpass.be.domain.wishlist.dto.WishlistListResponse;
import com.boardingpass.be.domain.wishlist.service.WishlistService;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Wishlist", description = "위시리스트 API")
@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

  private final WishlistService wishlistService;

  @Operation(summary = "위시리스트 담기")
  @PostMapping
  public ApiResponse<WishlistItemResponse> addWishlist(@Valid @RequestBody WishlistCreateRequest request) {
    return ApiResponse.onSuccess(wishlistService.addWishlist(request));
  }

  @Operation(summary = "위시리스트 조회")
  @GetMapping
  public ApiResponse<WishlistListResponse> getWishlist() {
    return ApiResponse.onSuccess(wishlistService.getWishlist());
  }

  @Operation(summary = "위시리스트 삭제")
  @DeleteMapping("/{productColorId}")
  public ApiResponse<WishlistDeleteResponse> removeWishlist(@PathVariable Long productColorId) {
    return ApiResponse.onSuccess(wishlistService.removeWishlist(productColorId));
  }
}
