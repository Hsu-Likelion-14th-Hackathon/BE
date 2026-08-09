package com.boardingpass.be.domain.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("""
      select distinct p from Product p
      left join fetch p.colors
      where p.popularityRank is not null
      order by p.popularityRank asc
      """)
  List<Product> findAllByPopularityRankAscWithColors();
}