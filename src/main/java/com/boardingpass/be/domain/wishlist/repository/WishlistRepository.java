package com.boardingpass.be.domain.wishlist.repository;

import com.boardingpass.be.domain.wishlist.entity.Wishlist;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

  @Query("""
      select w from Wishlist w
      join fetch w.productColor pc
      join fetch pc.product
      where w.user.id = :userId
      """)
  List<Wishlist> findByUserIdWithProduct(@Param("userId") Long userId);

  boolean existsByUserIdAndProductColorId(Long userId, Long productColorId);

  void deleteByUserIdAndProductColorId(Long userId, Long productColorId);

  @Query("select w.productColor.id from Wishlist w where w.user.id = :userId")
  Set<Long> findProductColorIdsByUserId(@Param("userId") Long userId);
}
