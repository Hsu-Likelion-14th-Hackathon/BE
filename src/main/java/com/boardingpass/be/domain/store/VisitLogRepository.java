package com.boardingpass.be.domain.store;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

  long countByStoreId(Long storeId);

  Optional<VisitLog> findByBoardingPassId(Long boardingPassId);

  @Query("""
      select v from VisitLog v
      join fetch v.user
      join fetch v.store
      join fetch v.boardingPass bp
      join fetch bp.user
      where v.id = :visitLogId
      """)
  Optional<VisitLog> findDetailById(@Param("visitLogId") Long visitLogId);
}