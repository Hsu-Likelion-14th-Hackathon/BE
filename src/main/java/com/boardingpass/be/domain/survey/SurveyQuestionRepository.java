package com.boardingpass.be.domain.survey;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {

  @EntityGraph(attributePaths = "options")
  List<SurveyQuestion> findByIsActiveTrueOrderByStepNoAsc();
}