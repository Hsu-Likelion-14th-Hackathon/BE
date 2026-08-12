package com.boardingpass.be.domain.boardingpass;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardingPassRepository extends JpaRepository<BoardingPass, Long> {

  boolean existsByPassCode(String passCode);

  Optional<BoardingPass> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}