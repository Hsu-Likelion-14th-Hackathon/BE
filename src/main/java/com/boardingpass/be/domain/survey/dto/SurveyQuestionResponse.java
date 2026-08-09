package com.boardingpass.be.domain.survey.dto;

import com.boardingpass.be.domain.survey.QuestionType;
import com.boardingpass.be.domain.survey.SurveyQuestion;
import java.util.Comparator;
import java.util.List;

public record SurveyQuestionResponse(
    Long surveyQuestionId,
    Integer stepNo,
    String content,
    QuestionType questionType,
    Boolean isRequired,
    List<SurveyOptionResponse> options
) {
  public static SurveyQuestionResponse from(SurveyQuestion question) {
    List<SurveyOptionResponse> options = question.getOptions().stream()
        .sorted(Comparator.comparing(o -> o.getOrderNo()))
        .map(SurveyOptionResponse::from)
        .toList();

    return new SurveyQuestionResponse(
        question.getId(),
        question.getStepNo(),
        question.getContent(),
        question.getQuestionType(),
        question.getIsRequired(),
        options
    );
  }
}