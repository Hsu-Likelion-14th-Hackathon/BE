package com.boardingpass.be.domain.bag.service;

import com.boardingpass.be.domain.bag.dto.ShoppingBagCreateRequest;
import com.boardingpass.be.domain.bag.dto.ShoppingBagDeleteResponse;
import com.boardingpass.be.domain.bag.dto.ShoppingBagItemResponse;
import com.boardingpass.be.domain.bag.dto.ShoppingBagListResponse;
import com.boardingpass.be.domain.bag.entity.ShoppingBagItem;
import com.boardingpass.be.domain.bag.repository.ShoppingBagItemRepository;
import com.boardingpass.be.domain.product.ProductSize;
import com.boardingpass.be.domain.product.ProductSizeRepository;
import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.domain.user.repository.UserRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingBagItemService {

  private final ShoppingBagItemRepository shoppingBagItemRepository;
  private final UserRepository userRepository;
  private final ProductSizeRepository productSizeRepository;

  @Transactional
  public ShoppingBagItemResponse addShoppingBagItem(ShoppingBagCreateRequest request) {
    Long userId = SecurityUtils.getCurrentUserId();

    ShoppingBagItem existing = shoppingBagItemRepository
        .findByUserIdAndProductSizeId(userId, request.productSizeId())
        .orElse(null);
    if (existing != null) {
      return ShoppingBagItemResponse.from(existing);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    ProductSize productSize = productSizeRepository.findById(request.productSizeId())
        .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_SIZE_NOT_FOUND));

    if (!productSize.hasStock(1)) {
      throw new GeneralException(ErrorStatus.STOCK_EXCEEDED);
    }

    ShoppingBagItem saved = shoppingBagItemRepository.save(
        ShoppingBagItem.builder()
            .user(user)
            .productSize(productSize)
            .build()
    );

    return ShoppingBagItemResponse.from(saved);
  }

  public ShoppingBagListResponse getShoppingBag() {
    Long userId = SecurityUtils.getCurrentUserId();

    return ShoppingBagListResponse.of(
        shoppingBagItemRepository.findByUserIdWithProduct(userId).stream()
            .map(ShoppingBagItemResponse::from)
            .toList()
    );
  }

  @Transactional
  public ShoppingBagDeleteResponse removeShoppingBagItem(Long shoppingBagItemId) {
    Long userId = SecurityUtils.getCurrentUserId();
    shoppingBagItemRepository.deleteByIdAndUserId(shoppingBagItemId, userId);
    return new ShoppingBagDeleteResponse(true);
  }
}
