package com.boardingpass.be.domain.product;

import com.boardingpass.be.domain.product.dto.ProductDetailResponse;
import com.boardingpass.be.domain.product.dto.ProductSummaryResponse;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @Operation(summary = "상품 목록 조회")
  @GetMapping
  public ApiResponse<List<ProductSummaryResponse>> getProducts(
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String size
  ) {
    return ApiResponse.onSuccess(productService.getProducts(sort, size));
  }

  @Operation(summary = "상품 상세 조회")
  @GetMapping("/{productId}")
  public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
    return ApiResponse.onSuccess(productService.getProductDetail(productId));
  }
}
