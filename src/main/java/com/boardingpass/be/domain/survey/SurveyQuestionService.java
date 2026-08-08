package com.boardingpass.be.domain.survey;

import com.boardingpass.be.domain.survey.dto.SurveyQuestionResponse;
import com.boardingpass.be.domain.survey.dto.SurveyQuestionsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyQuestionService {

  private final SurveyQuestionRepository surveyQuestionRepository;

  public SurveyQuestionsResponse getActiveQuestions() {
    List<SurveyQuestionResponse> questions = surveyQuestionRepository
        .findByIsActiveTrueOrderByStepNoAsc()
        .stream()
        .map(SurveyQuestionResponse::from)
        .toList();

    return SurveyQuestionsResponse.of(questions);
  }
}