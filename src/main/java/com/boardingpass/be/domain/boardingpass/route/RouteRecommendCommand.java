package com.boardingpass.be.domain.boardingpass.route;

import com.boardingpass.be.domain.floor.Floor;
import com.boardingpass.be.domain.survey.SurveyOption;
import java.util.List;

public record RouteRecommendCommand(
    List<Floor> floorsOrdered,
    SurveyOption q2Option,
    SurveyOption q3Option,
    SurveyOption q4Option,
    SurveyOption q5Option,
    String textAnswer,
    List<String> productNames
) {
}