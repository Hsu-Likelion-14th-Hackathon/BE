package com.boardingpass.be.domain.survey;

import com.boardingpass.be.domain.survey.dto.SurveyQuestionsResponse;
import com.boardingpass.be.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Survey", description = "설문 API")
@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class SurveyQuestionController {

  private final SurveyQuestionService surveyQuestionService;

  @Operation(summary = "설문 문항 조회")
  @GetMapping("/questions")
  public ApiResponse<SurveyQuestionsResponse> getQuestions() {
    return ApiResponse.onSuccess(surveyQuestionService.getActiveQuestions());
  }
}