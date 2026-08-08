package com.boardingpass.be.domain.survey.dto;

import java.util.List;

public record SurveyQuestionsResponse(
    List<SurveyQuestionResponse> questions
) {
  public static SurveyQuestionsResponse of(List<SurveyQuestionResponse> questions) {
    return new SurveyQuestionsResponse(questions);
  }
}