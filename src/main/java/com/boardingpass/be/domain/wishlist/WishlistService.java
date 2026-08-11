package com.boardingpass.be.domain.wishlist;

import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductColorRepository;
import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.domain.user.repository.UserRepository;
import com.boardingpass.be.domain.wishlist.dto.WishlistCreateRequest;
import com.boardingpass.be.domain.wishlist.dto.WishlistItemResponse;
import com.boardingpass.be.domain.wishlist.dto.WishlistListResponse;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

  private final WishlistRepository wishlistRepository;
  private final UserRepository userRepository;
  private final ProductColorRepository productColorRepository;

  @Transactional
  public WishlistItemResponse addWishlist(WishlistCreateRequest request) {
    Long userId = SecurityUtils.getCurrentUserId();
    ProductColor productColor = productColorRepository.findById(request.productColorId())
        .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_COLOR_NOT_FOUND));

    if (wishlistRepository.existsByUserIdAndProductColorId(userId, productColor.getId())) {
      return WishlistItemResponse.from(productColor);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

    wishlistRepository.save(
        Wishlist.builder()
            .user(user)
            .productColor(productColor)
            .build()
    );

    return WishlistItemResponse.from(productColor);
  }

  public WishlistListResponse getWishlist() {
    Long userId = SecurityUtils.getCurrentUserId();

    return WishlistListResponse.of(
        wishlistRepository.findByUserIdWithProduct(userId).stream()
            .map(WishlistItemResponse::from)
            .toList()
    );
  }

  @Transactional
  public void removeWishlist(Long productColorId) {
    Long userId = SecurityUtils.getCurrentUserId();
    wishlistRepository.deleteByUserIdAndProductColorId(userId, productColorId);
  }
}
