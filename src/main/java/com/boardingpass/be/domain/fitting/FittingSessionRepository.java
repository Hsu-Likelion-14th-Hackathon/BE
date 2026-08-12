package com.boardingpass.be.domain.fitting;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FittingSessionRepository extends JpaRepository<FittingSession, Long> {

  Optional<FittingSession> findByIdAndUserId(Long id, Long userId);
}
