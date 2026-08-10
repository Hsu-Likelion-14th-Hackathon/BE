package com.boardingpass.be.domain.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("""
      select distinct p from Product p
      left join fetch p.colors
      where p.popularityRank is not null
      order by p.popularityRank asc
      """)
  List<Product> findAllByPopularityRankAscWithColors();

  @Query("""
      select distinct p from Product p
      left join fetch p.colors
      where p.popularityRank is not null
        and (:sizeLabel is null or exists (
            select 1 from ProductSize ps
            where ps.productColor.product = p and ps.sizeLabel = :sizeLabel
        ))
      order by p.popularityRank asc
      """)
  List<Product> findAllByPopularityRankAscAndSizeLabel(@Param("sizeLabel") String sizeLabel);

  @Query("""
      select distinct p from Product p
      left join fetch p.colors
      where p.popularityRank is not null
        and (:sizeLabel is null or exists (
            select 1 from ProductSize ps
            where ps.productColor.product = p and ps.sizeLabel = :sizeLabel
        ))
      order by p.price asc
      """)
  List<Product> findAllByPriceAscAndSizeLabel(@Param("sizeLabel") String sizeLabel);

  @Query("""
      select distinct p from Product p
      left join fetch p.colors
      where p.popularityRank is not null
        and (:sizeLabel is null or exists (
            select 1 from ProductSize ps
            where ps.productColor.product = p and ps.sizeLabel = :sizeLabel
        ))
      order by p.price desc
      """)
  List<Product> findAllByPriceDescAndSizeLabel(@Param("sizeLabel") String sizeLabel);

  @Query("""
      select p from Product p
      left join fetch p.colors
      where p.id = :productId
      """)
  Optional<Product> findByIdWithColors(@Param("productId") Long productId);
}
