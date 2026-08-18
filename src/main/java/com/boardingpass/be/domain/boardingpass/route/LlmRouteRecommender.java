package com.boardingpass.be.domain.boardingpass.route;

import com.boardingpass.be.domain.floor.Floor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class LlmRouteRecommender implements RouteRecommender {

  private static final String SYSTEM_PROMPT = """
      당신은 MCM Haus 전시의 도슨트입니다.
      관람객의 설문 응답을 근거로, 각 층을 추천하는 이유를 한 문장으로 작성하세요.

      규칙:
      - 각 reason 은 40자 이내 한국어 한 문장
      - 설문 응답이나 위시리스트 상품을 근거로 구체적으로 언급할 것
      - 존댓말 사용, 광고 문구 금지
      - 주어진 층 코드 외의 코드를 만들지 말 것

      출력은 아래 JSON 형식만:
      {"reasons":[{"code":"JOURNEY","reason":"..."}]}
      """;

  private final RuleBasedRouteRecommender ruleBasedRouteRecommender;
  private final WebClient webClient;
  private final ObjectMapper objectMapper;

  @Value("${openai.api-key:}")
  private String apiKey;

  @Value("${openai.model:gpt-4o-mini}")
  private String model;

  @Value("${openai.timeout-seconds:5}")
  private long timeoutSeconds;

  @Override
  public List<RecommendedStep> recommend(RouteRecommendCommand command) {
    List<RecommendedStep> baseSteps = ruleBasedRouteRecommender.recommend(command);

    if (apiKey == null || apiKey.isBlank()) {
      return baseSteps;
    }

    List<RecommendedStep> recommended = baseSteps.stream()
        .filter(RecommendedStep::recommended)
        .toList();
    if (recommended.isEmpty()) {
      return baseSteps;
    }

    try {
      Map<String, String> llmReasons = callOpenAi(command, recommended);
      Set<String> allowed = recommended.stream()
          .map(step -> step.floor().getCode())
          .collect(Collectors.toSet());

      return baseSteps.stream()
          .map(step -> {
            if (!step.recommended()) {
              return step;
            }
            String reason = llmReasons.get(step.floor().getCode());
            if (reason == null || reason.isBlank() || reason.length() > 100
                || !allowed.contains(step.floor().getCode())) {
              return step;
            }
            return step.withReason(reason);
          })
          .toList();
    } catch (Exception e) {
      log.warn("OpenAI reason generation failed. Using rule-based fallback.", e);
      return baseSteps;
    }
  }

  private Map<String, String> callOpenAi(
      RouteRecommendCommand command,
      List<RecommendedStep> recommended) throws Exception {
    String userContent = buildUserContent(command, recommended);

    Map<String, Object> body = Map.of(
        "model", model,
        "response_format", Map.of("type", "json_object"),
        "messages", List.of(
            Map.of("role", "system", "content", SYSTEM_PROMPT),
            Map.of("role", "user", "content", userContent)));

    String raw = webClient.post()
        .uri("https://api.openai.com/v1/chat/completions")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .timeout(Duration.ofSeconds(timeoutSeconds))
        .block();

    JsonNode root = objectMapper.readTree(raw);
    String content = root.path("choices").path(0).path("message").path("content").asText();
    JsonNode reasons = objectMapper.readTree(content).path("reasons");

    Map<String, String> result = new HashMap<>();
    if (!reasons.isArray()) {
      return result;
    }
    for (JsonNode node : reasons) {
      String code = node.path("code").asText(null);
      String reason = node.path("reason").asText(null);
      if (code == null || reason == null || reason.isBlank() || reason.length() > 100) {
        continue;
      }
      result.put(code, reason);
    }
    return result;
  }

  private String buildUserContent(RouteRecommendCommand command, List<RecommendedStep> recommended) {
    String floors = recommended.stream()
        .map(RecommendedStep::floor)
        .map(f -> "- floorNo=" + f.getFloorNo()
            + ", code=" + f.getCode()
            + ", title=" + f.getTitle()
            + ", tagline=" + nullToEmpty(f.getTagline()))
        .collect(Collectors.joining("\n"));

    String products = command.productNames().stream()
        .limit(5)
        .collect(Collectors.joining(", "));

    return """
        추천 층:
        %s

        설문:
        - Q2: %s
        - Q3: %s
        - Q4: %s

        스냅샷 상품: %s
        """.formatted(
        floors,
        command.q2Option().getLabel(),
        command.q3Option().getLabel(),
        command.q4Option().getLabel(),
        products.isBlank() ? "없음" : products);
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
}