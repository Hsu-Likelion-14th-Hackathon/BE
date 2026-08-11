package com.boardingpass.be.domain.bag;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShoppingBagItemRepository extends JpaRepository<ShoppingBagItem, Long> {

  @Query("""
      select s from ShoppingBagItem s
      join fetch s.productSize ps
      join fetch ps.productColor pc
      join fetch pc.product
      where s.user.id = :userId
      """)
  List<ShoppingBagItem> findByUserIdWithProduct(@Param("userId") Long userId);

  boolean existsByUserIdAndProductSizeId(Long userId, Long productSizeId);

  void deleteByIdAndUserId(Long id, Long userId);
}
