package com.boardingpass.be.domain.boardingpass.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BoardingPassIssueRequest(
    @NotNull Boolean dataConsent,
    @NotNull @Valid List<SurveyAnswerRequest> answers
) {
}