package com.boardingpass.be.domain.passport;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassportRepository extends JpaRepository<Passport, Long> {

  @EntityGraph(attributePaths = "user")
  Optional<Passport> findByUserId(Long userId);
}