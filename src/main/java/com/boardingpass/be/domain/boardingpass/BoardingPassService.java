package com.boardingpass.be.domain.boardingpass;

import com.boardingpass.be.domain.bag.ShoppingBagItem;
import com.boardingpass.be.domain.bag.ShoppingBagItemRepository;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassIssueRequest;
import com.boardingpass.be.domain.boardingpass.dto.BoardingPassIssueResponse;
import com.boardingpass.be.domain.boardingpass.dto.SurveyAnswerRequest;
import com.boardingpass.be.domain.boardingpass.route.RecommendedStep;
import com.boardingpass.be.domain.boardingpass.route.RouteRecommendCommand;
import com.boardingpass.be.domain.boardingpass.route.RouteRecommender;
import com.boardingpass.be.domain.floor.Floor;
import com.boardingpass.be.domain.floor.FloorRepository;
import com.boardingpass.be.domain.product.Product;
import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductRepository;
import com.boardingpass.be.domain.store.Store;
import com.boardingpass.be.domain.store.StoreRepository;
import com.boardingpass.be.domain.survey.QuestionType;
import com.boardingpass.be.domain.survey.SurveyOption;
import com.boardingpass.be.domain.survey.SurveyQuestion;
import com.boardingpass.be.domain.survey.SurveyQuestionRepository;
import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.domain.user.UserRepository;
import com.boardingpass.be.domain.wishlist.Wishlist;
import com.boardingpass.be.domain.wishlist.WishlistRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.security.SecurityUtils;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardingPassService {

  private static final String PASS_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int PASS_CODE_LENGTH = 8;
  private static final int MAX_TEXT_ANSWER_LENGTH = 200;
  private static final int SURVEY_ITEM_LIMIT = 3;

  private final UserRepository userRepository;
  private final SurveyQuestionRepository surveyQuestionRepository;
  private final BoardingPassRepository boardingPassRepository;
  private final BoardingPassSurveyRepository boardingPassSurveyRepository;
  private final BoardingPassItemRepository boardingPassItemRepository;
  private final RouteStepRepository routeStepRepository;
  private final WishlistRepository wishlistRepository;
  private final ShoppingBagItemRepository shoppingBagItemRepository;
  private final ProductRepository productRepository;
  private final StoreRepository storeRepository;
  private final FloorRepository floorRepository;
  private final RouteRecommender routeRecommender;
  private final SecureRandom secureRandom = new SecureRandom();

  @Transactional
  public BoardingPassIssueResponse issue(BoardingPassIssueRequest request) {
    Long userId = SecurityUtils.getCurrentUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus._UNAUTHORIZED));

    List<SurveyQuestion> activeQuestions =
        surveyQuestionRepository.findByIsActiveTrueOrderByStepNoAsc();
    ValidatedSurvey validated = validateAnswers(activeQuestions, request.answers());

    BoardingPass boardingPass = boardingPassRepository.saveAndFlush(
        BoardingPass.builder()
            .user(user)
            .passCode(generateUniquePassCode())
            .status(BoardingPassStatus.ISSUED)
            .dataConsent(Boolean.TRUE.equals(request.dataConsent()))
            .build()
    );

    saveSurveys(boardingPass, validated);
    List<BoardingPassItem> items = createItems(boardingPass, user, boardingPass.getDataConsent());

    List<Floor> floors = loadFloorsOrdered();
    List<String> productNames = items.stream()
        .map(item -> item.getProductColor().getProduct().getName())
        .toList();

    List<RecommendedStep> steps = routeRecommender.recommend(
        new RouteRecommendCommand(
            floors,
            validated.q2Option(),
            validated.q3Option(),
            validated.q4Option(),
            validated.q5Option(),
            validated.textAnswer(),
            productNames
        )
    );
    saveRouteSteps(boardingPass, steps);

    return BoardingPassIssueResponse.of(boardingPass, items);
  }

  private ValidatedSurvey validateAnswers(
      List<SurveyQuestion> activeQuestions,
      List<SurveyAnswerRequest> answers
  ) {
    if (answers == null) {
      throw new GeneralException(ErrorStatus.SURVEY_INCOMPLETE);
    }

    Map<Long, SurveyQuestion> questionById = activeQuestions.stream()
        .collect(Collectors.toMap(SurveyQuestion::getId, Function.identity()));

    Map<Long, SurveyAnswerRequest> answerByQuestionId = new HashMap<>();
    for (SurveyAnswerRequest answer : answers) {
      if (answer.surveyQuestionId() == null || !questionById.containsKey(answer.surveyQuestionId())) {
        throw new GeneralException(ErrorStatus.INVALID_SURVEY_ANSWER);
      }
      if (answerByQuestionId.put(answer.surveyQuestionId(), answer) != null) {
        throw new GeneralException(ErrorStatus.INVALID_SURVEY_ANSWER);
      }
    }

    for (SurveyQuestion question : activeQuestions) {
      if (Boolean.TRUE.equals(question.getIsRequired())
          && !answerByQuestionId.containsKey(question.getId())) {
        throw new GeneralException(ErrorStatus.SURVEY_INCOMPLETE);
      }
    }

    Map<Long, SurveyOption> selectedOptionByQuestionId = new HashMap<>();
    String textAnswer = null;

    for (SurveyAnswerRequest answer : answerByQuestionId.values()) {
      SurveyQuestion question = questionById.get(answer.surveyQuestionId());
      String rawText = answer.textAnswer();
      if (rawText != null && rawText.length() > MAX_TEXT_ANSWER_LENGTH) {
        throw new GeneralException(ErrorStatus.INVALID_SURVEY_ANSWER);
      }

      if (question.getQuestionType() == QuestionType.SINGLE_SELECT) {
        if (answer.surveyOptionId() == null) {
          throw new GeneralException(ErrorStatus.INVALID_SURVEY_ANSWER);
        }
        SurveyOption option = question.getOptions().stream()
            .filter(o -> o.getId().equals(answer.surveyOptionId()))
            .findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_SURVEY_ANSWER));
        selectedOptionByQuestionId.put(question.getId(), option);
      } else if (question.getQuestionType() == QuestionType.TEXT) {
        if (answer.surveyOptionId() != null) {
          throw new GeneralException(ErrorStatus.INVALID_SURVEY_ANSWER);
        }
        textAnswer = rawText;
      }
    }

    SurveyOption q2 = requiredOptionByStep(activeQuestions, selectedOptionByQuestionId, 2);
    SurveyOption q3 = requiredOptionByStep(activeQuestions, selectedOptionByQuestionId, 3);
    SurveyOption q4 = requiredOptionByStep(activeQuestions, selectedOptionByQuestionId, 4);
    SurveyOption q5 = requiredOptionByStep(activeQuestions, selectedOptionByQuestionId, 5);

    return new ValidatedSurvey(
        activeQuestions,
        answerByQuestionId,
        selectedOptionByQuestionId,
        textAnswer,
        q2, q3, q4, q5
    );
  }

  private SurveyOption requiredOptionByStep(
      List<SurveyQuestion> questions,
      Map<Long, SurveyOption> selected,
      int stepNo
  ) {
    SurveyQuestion question = questions.stream()
        .filter(q -> q.getStepNo() == stepNo)
        .findFirst()
        .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_SURVEY_ANSWER));
    SurveyOption option = selected.get(question.getId());
    if (option == null) {
      throw new GeneralException(ErrorStatus.SURVEY_INCOMPLETE);
    }
    return option;
  }

  private void saveSurveys(BoardingPass boardingPass, ValidatedSurvey validated) {
    for (SurveyQuestion question : validated.activeQuestions()) {
      SurveyAnswerRequest answer = validated.answerByQuestionId().get(question.getId());
      if (answer == null) {
        continue;
      }
      boardingPassSurveyRepository.save(
          BoardingPassSurvey.builder()
              .boardingPass(boardingPass)
              .surveyQuestion(question)
              .surveyOption(validated.selectedOptionByQuestionId().get(question.getId()))
              .textAnswer(
                  question.getQuestionType() == QuestionType.TEXT ? validated.textAnswer() : null)
              .build()
      );
    }
  }

  private List<BoardingPassItem> createItems(
      BoardingPass boardingPass,
      User user,
      boolean dataConsent
  ) {
    LinkedHashMap<Long, ItemSource> colorSource = new LinkedHashMap<>();

    if (dataConsent) {
      List<Wishlist> wishlists = wishlistRepository.findByUserIdWithProduct(user.getId());
      for (Wishlist wishlist : wishlists) {
        colorSource.putIfAbsent(wishlist.getProductColor().getId(), ItemSource.WISHLIST);
      }
      List<ShoppingBagItem> bagItems = shoppingBagItemRepository.findByUserIdWithProduct(user.getId());
      for (ShoppingBagItem bagItem : bagItems) {
        Long colorId = bagItem.getProductSize().getProductColor().getId();
        colorSource.putIfAbsent(colorId, ItemSource.BAG);
      }
    }

    List<BoardingPassItem> items = new ArrayList<>();
    if (!colorSource.isEmpty()) {
      Map<Long, ProductColor> colorMap = new HashMap<>();
      if (dataConsent) {
        wishlistRepository.findByUserIdWithProduct(user.getId())
            .forEach(w -> colorMap.put(w.getProductColor().getId(), w.getProductColor()));
        shoppingBagItemRepository.findByUserIdWithProduct(user.getId())
            .forEach(b -> colorMap.put(
                b.getProductSize().getProductColor().getId(),
                b.getProductSize().getProductColor()));
      }
      for (Map.Entry<Long, ItemSource> entry : colorSource.entrySet()) {
        ProductColor color = colorMap.get(entry.getKey());
        if (color == null) {
          continue;
        }
        items.add(boardingPassItemRepository.save(
            BoardingPassItem.builder()
                .boardingPass(boardingPass)
                .productColor(color)
                .source(entry.getValue())
                .build()
        ));
      }
    }

    if (items.isEmpty()) {
      List<Product> products = productRepository.findAllByPopularityRankAscWithColors();
      int count = 0;
      for (Product product : products) {
        if (count >= SURVEY_ITEM_LIMIT) {
          break;
        }
        ProductColor defaultColor = product.getColors().stream()
            .filter(ProductColor::isDefault)
            .findFirst()
            .orElse(null);
        if (defaultColor == null) {
          continue;
        }
        items.add(boardingPassItemRepository.save(
            BoardingPassItem.builder()
                .boardingPass(boardingPass)
                .productColor(defaultColor)
                .source(ItemSource.SURVEY)
                .build()
        ));
        count++;
      }
    }
    return items;
  }

  private List<Floor> loadFloorsOrdered() {
    Store store = storeRepository.findFirstByOrderByIdAsc()
        .orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));
    return floorRepository.findByStoreIdOrderByFloorNoAsc(store.getId());
  }

  private void saveRouteSteps(BoardingPass boardingPass, List<RecommendedStep> steps) {
    for (RecommendedStep step : steps) {
      routeStepRepository.save(
          RouteStep.builder()
              .boardingPass(boardingPass)
              .floor(step.floor())
              .sequence(step.sequence())
              .reason(step.reason())
              .isRecommended(step.recommended())
              .build()
      );
    }
  }

  private String generateUniquePassCode() {
    for (int i = 0; i < 20; i++) {
      String code = "MCM-" + randomSuffix();
      if (!boardingPassRepository.existsByPassCode(code)) {
        return code;
      }
    }
    throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
  }

  private String randomSuffix() {
    StringBuilder sb = new StringBuilder(PASS_CODE_LENGTH);
    for (int i = 0; i < PASS_CODE_LENGTH; i++) {
      sb.append(PASS_CODE_CHARS.charAt(secureRandom.nextInt(PASS_CODE_CHARS.length())));
    }
    return sb.toString();
  }

  private record ValidatedSurvey(
      List<SurveyQuestion> activeQuestions,
      Map<Long, SurveyAnswerRequest> answerByQuestionId,
      Map<Long, SurveyOption> selectedOptionByQuestionId,
      String textAnswer,
      SurveyOption q2Option,
      SurveyOption q3Option,
      SurveyOption q4Option,
      SurveyOption q5Option
  ) {
  }
}