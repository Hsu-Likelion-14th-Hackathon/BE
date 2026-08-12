package com.boardingpass.be.domain.store;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

  long countByStoreId(Long storeId);

  Optional<VisitLog> findByBoardingPassId(Long boardingPassId);
}