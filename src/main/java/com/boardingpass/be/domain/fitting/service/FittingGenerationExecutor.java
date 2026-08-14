package com.boardingpass.be.domain.fitting.service;

import com.boardingpass.be.domain.credit.CreditReason;
import com.boardingpass.be.domain.credit.CreditRefType;
import com.boardingpass.be.domain.credit.CreditService;
import com.boardingpass.be.domain.fitting.FittingStatus;
import com.boardingpass.be.domain.fitting.entity.FittingSession;
import com.boardingpass.be.domain.fitting.generator.FittingGenerationCommand;
import com.boardingpass.be.domain.fitting.generator.FittingGenerationResult;
import com.boardingpass.be.domain.fitting.generator.FittingImageGenerator;
import com.boardingpass.be.domain.fitting.repository.FittingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FittingGenerationExecutor {

  private final FittingSessionRepository fittingSessionRepository;
  private final FittingImageGenerator fittingImageGenerator;
  private final CreditService creditService;

  @Async
  @Transactional
  public void generate(Long fittingSessionId) {
    FittingSession session = fittingSessionRepository.findById(fittingSessionId).orElse(null);
    if (session == null || session.getStatus() != FittingStatus.PENDING) {
      return;
    }

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
