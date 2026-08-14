package com.boardingpass.be.domain.boardingpass.route;

import com.boardingpass.be.domain.floor.Floor;
import com.boardingpass.be.domain.survey.SurveyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RuleBasedRouteRecommender implements RouteRecommender {

  private static final Map<String, String> STYLE_TO_FLOOR = Map.of(
      "CLASSIC", "ORIGIN",
      "PRACTICAL", "JOURNEY",
      "TRENDY", "EMBLEM",
      "MINIMAL", "HORIZON");

  private static final Map<String, Integer> TIME_CUTOFF = Map.of(
      "QUICK", 2,
      "STANDARD", 3);

  @Override
  public List<RecommendedStep> recommend(RouteRecommendCommand command) {
    List<Floor> floors = command.floorsOrdered();
    Map<String, Floor> floorByCode = floors.stream()
        .collect(Collectors.toMap(Floor::getCode, f -> f, (a, b) -> a));

    String q2Tag = command.q2Option().getTag();
    final String primaryCode = floorByCode.containsKey(q2Tag)
        ? q2Tag
        : floors.get(0).getCode();

    String q3Mapped = STYLE_TO_FLOOR.getOrDefault(command.q3Option().getTag(), "EMBLEM");
    if (q3Mapped.equals(primaryCode)) {
      q3Mapped = "EMBLEM";
    }

    final String secondaryCode = floorByCode.containsKey(q3Mapped)
        ? q3Mapped
        : floors.stream()
            .map(Floor::getCode)
            .filter(code -> !code.equals(primaryCode))
            .findFirst()
            .orElse(primaryCode);

    String q4Tag = command.q4Option().getTag();
    int cutoff = "FULL".equals(q4Tag)
        ? floors.size()
        : TIME_CUTOFF.getOrDefault(q4Tag, 3);
    cutoff = Math.min(cutoff, floors.size());

    LinkedHashSet<String> recommendedCodes = new LinkedHashSet<>();
    recommendedCodes.add(primaryCode);
    recommendedCodes.add(secondaryCode);
    for (Floor floor : floors) {
      if (recommendedCodes.size() >= cutoff) {
        break;
      }
      recommendedCodes.add(floor.getCode());
    }

    List<String> ranked = new ArrayList<>(recommendedCodes);
    Set<String> recommendedSet = Set.copyOf(recommendedCodes);

    List<RecommendedStep> steps = new ArrayList<>();
    for (int i = 0; i < floors.size(); i++) {
      Floor floor = floors.get(i);
      boolean recommended = recommendedSet.contains(floor.getCode());
      String reason = null;
      if (recommended) {
        int rank = ranked.indexOf(floor.getCode());
        reason = fallbackReason(rank, floor, command.q2Option(), command.q3Option());
      }
      steps.add(new RecommendedStep(floor, i + 1, recommended, reason));
    }
    return steps;
  }

  public String fallbackReason(int rank, Floor floor, SurveyOption q2, SurveyOption q3) {
    if (rank == 0) {
      return q2.getLabel() + "에 관심을 보이셔서 " + floor.getTitle() + " 층을 먼저 안내합니다.";
    }
    if (rank == 1) {
      return q3.getLabel() + " 스타일과 잘 맞는 층이에요.";
    }
    return "여정을 완성하는 층입니다.";
  }
}