package com.boardingpass.be.domain.boardingpass;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteStepRepository extends JpaRepository<RouteStep, Long> {

  @EntityGraph(attributePaths = "floor")
  List<RouteStep> findByBoardingPassIdOrderBySequenceAsc(Long boardingPassId);
}