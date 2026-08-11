package com.boardingpass.be.domain.bag;

import com.boardingpass.be.domain.bag.dto.ShoppingBagCreateRequest;
import com.boardingpass.be.domain.bag.dto.ShoppingBagDeleteResponse;
import com.boardingpass.be.domain.bag.dto.ShoppingBagItemResponse;
import com.boardingpass.be.domain.bag.dto.ShoppingBagListResponse;
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

@Tag(name = "ShoppingBag", description = "쇼핑백 API")
@RestController
@RequestMapping("/shopping-bag")
@RequiredArgsConstructor
public class ShoppingBagItemController {

  private final ShoppingBagItemService shoppingBagItemService;

  @Operation(summary = "쇼핑백 담기")
  @PostMapping
  public ApiResponse<ShoppingBagItemResponse> addShoppingBagItem(
      @Valid @RequestBody ShoppingBagCreateRequest request
  ) {
    return ApiResponse.onSuccess(shoppingBagItemService.addShoppingBagItem(request));
  }

  @Operation(summary = "쇼핑백 조회")
  @GetMapping
  public ApiResponse<ShoppingBagListResponse> getShoppingBag() {
    return ApiResponse.onSuccess(shoppingBagItemService.getShoppingBag());
  }

  @Operation(summary = "쇼핑백 항목 삭제")
  @DeleteMapping("/{shoppingBagItemId}")
  public ApiResponse<ShoppingBagDeleteResponse> removeShoppingBagItem(@PathVariable Long shoppingBagItemId) {
    return ApiResponse.onSuccess(shoppingBagItemService.removeShoppingBagItem(shoppingBagItemId));
  }
}
