package com.boardingpass.be.domain.boardingpass;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardingPassRepository extends JpaRepository<BoardingPass, Long> {

  boolean existsByPassCode(String passCode);
}