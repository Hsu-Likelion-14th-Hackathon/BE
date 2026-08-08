package com.boardingpass.be.domain.floor;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FloorRepository extends JpaRepository<Floor, Long> {

  List<Floor> findByStoreIdOrderByFloorNoAsc(Long storeId);

  @Query("""
      select distinct f
      from Floor f
      left join fetch f.contents c
      left join fetch c.product
      where f.id = :floorId
      """)
  Optional<Floor> findDetailById(@Param("floorId") Long floorId);
}