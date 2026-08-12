package com.boardingpass.be.domain.fitting.service;

import com.boardingpass.be.domain.credit.CreditPolicy;
import com.boardingpass.be.domain.credit.CreditReason;
import com.boardingpass.be.domain.credit.CreditRefType;
import com.boardingpass.be.domain.credit.CreditService;
import com.boardingpass.be.domain.fitting.FittingStatus;
import com.boardingpass.be.domain.fitting.dto.FittingSessionCreateRequest;
import com.boardingpass.be.domain.fitting.dto.FittingSessionResponse;
import com.boardingpass.be.domain.fitting.entity.FittingSession;
import com.boardingpass.be.domain.fitting.generator.FittingGenerationCommand;
import com.boardingpass.be.domain.fitting.generator.FittingGenerationResult;
import com.boardingpass.be.domain.fitting.generator.FittingImageGenerator;
import com.boardingpass.be.domain.fitting.repository.FittingSessionRepository;
import com.boardingpass.be.domain.product.ProductColor;
import com.boardingpass.be.domain.product.ProductColorRepository;
import com.boardingpass.be.domain.storage.AzureBlobStorageService;
import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.domain.user.repository.UserRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FittingSessionService {

  private final FittingSessionRepository fittingSessionRepository;
  private final UserRepository userRepository;
  private final ProductColorRepository productColorRepository;
  private final AzureBlobStorageService azureBlobStorageService;
  private final FittingImageGenerator fittingImageGenerator;
  private final CreditService creditService;

  @Transactional
  public FittingSessionResponse createFittingSession(FittingSessionCreateRequest request) {
    Long userId = SecurityUtils.getCurrentUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    ProductColor productColor = productColorRepository.findById(request.productColorId())
        .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_COLOR_NOT_FOUND));

    String sourceImageUrl = resolveSourceImageUrl(request.fileKey(), user);

    FittingSession session = fittingSessionRepository.save(
        FittingSession.builder()
            .user(user)
            .productColor(productColor)
            .sourceImageUrl(sourceImageUrl)
            .creditCost(CreditPolicy.FITTING_COST)
            .status(FittingStatus.PENDING)
            .build()
    );

    creditService.spend(
        userId,
        CreditPolicy.FITTING_COST,
        CreditReason.FITTING,
        CreditRefType.FITTING_SESSION,
        session.getId(),
        "가상 피팅"
    );

    return FittingSessionResponse.from(session);
  }

  private String resolveSourceImageUrl(String fileKey, User user) {
    if (fileKey != null && !fileKey.isBlank()) {
      String imageUrl = azureBlobStorageService.createReadUrl(fileKey);
      user.updateDefaultBodyImage(imageUrl);
      return imageUrl;
    }
    if (!user.hasDefaultBodyImage()) {
      throw new GeneralException(ErrorStatus.BODY_IMAGE_NOT_FOUND);
    }
    return user.getDefaultBodyImageUrl();
  }

  @Transactional
  public FittingSessionResponse getFittingSession(Long fittingSessionId) {
    Long userId = SecurityUtils.getCurrentUserId();
    FittingSession session = fittingSessionRepository.findByIdAndUserId(fittingSessionId, userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.FITTING_SESSION_NOT_FOUND));

    if (session.getStatus() == FittingStatus.PENDING) {
      processGeneration(session);
    }

    return FittingSessionResponse.from(session);
  }

  private void processGeneration(FittingSession session) {
    FittingGenerationResult result = fittingImageGenerator.generate(
        new FittingGenerationCommand(session.getSourceImageUrl(), session.getProductColor())
    );

    if (result.success()) {
      session.complete(result.resultImageUrl());
      return;
    }

    session.fail();
    refundCredit(session);
  }

  private void refundCredit(FittingSession session) {
    creditService.earn(
        session.getUser().getId(),
        session.getCreditCost(),
        CreditReason.REFUND,
        CreditRefType.FITTING_SESSION,
        session.getId(),
        "가상 피팅 실패 환급"
    );
  }
}
