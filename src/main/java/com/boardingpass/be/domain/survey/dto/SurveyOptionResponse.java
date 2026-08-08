package com.boardingpass.be.domain.survey.dto;

import com.boardingpass.be.domain.survey.SurveyOption;

public record SurveyOptionResponse(
    Long surveyOptionId,
    String label,
    String description,
    Integer orderNo
) {
  public static SurveyOptionResponse from(SurveyOption option) {
    return new SurveyOptionResponse(
        option.getId(),
        option.getLabel(),
        option.getDescription(),
        option.getOrderNo()
    );
  }
}