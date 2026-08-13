package com.boardingpass.be.domain.product;

import com.boardingpass.be.domain.product.dto.ProductDetailResponse;
import com.boardingpass.be.domain.product.dto.ProductSummaryResponse;
import com.boardingpass.be.domain.wishlist.repository.WishlistRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.security.SecurityUtils;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;
  private final WishlistRepository wishlistRepository;

  public List<ProductSummaryResponse> getProducts(String sort, String size) {
    ProductSortType sortType = ProductSortType.from(sort);
    String sizeLabel = (size == null || size.isBlank()) ? null : size;

    List<Product> products = switch (sortType) {
      case PRICE_ASC -> productRepository.findAllByPriceAscAndSizeLabel(sizeLabel);
      case PRICE_DESC -> productRepository.findAllByPriceDescAndSizeLabel(sizeLabel);
      case POPULARITY -> productRepository.findAllByPopularityRankAscAndSizeLabel(sizeLabel);
    };

    Long userId = SecurityUtils.getCurrentUserId();
    Set<Long> wishedProductColorIds = wishlistRepository.findProductColorIdsByUserId(userId);

    return products.stream()
        .flatMap(product -> product.getColors().stream())
        .map(color -> ProductSummaryResponse.from(color, wishedProductColorIds))
        .toList();
  }

  public ProductDetailResponse getProductDetail(Long productId) {
    Product product = productRepository.findByIdWithColors(productId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

    if (product.getColors().isEmpty()) {
      throw new GeneralException(ErrorStatus.PRODUCT_COLOR_NOT_FOUND);
    }

    return ProductDetailResponse.from(product);
  }
}
