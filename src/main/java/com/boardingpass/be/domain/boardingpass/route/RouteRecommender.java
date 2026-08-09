package com.boardingpass.be.domain.boardingpass.route;


import java.util.List;

public interface RouteRecommender {

  List<RecommendedStep> recommend(RouteRecommendCommand command);
}
