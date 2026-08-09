package com.boardingpass.be.domain.boardingpass.dto;

import jakarta.validation.constraints.NotNull;

public record SurveyAnswerRequest(
    @NotNull Long surveyQuestionId,
    Long surveyOptionId,
    String textAnswer
) {
}