package com.boardingpass.be.domain.passport;

import com.boardingpass.be.domain.boardingpass.RouteStep;
import com.boardingpass.be.domain.boardingpass.RouteStepRepository;
import com.boardingpass.be.domain.credit.CreditLedger;
import com.boardingpass.be.domain.credit.CreditLedgerRepository;
import com.boardingpass.be.domain.passport.dto.PassportCreditsResponse;
import com.boardingpass.be.domain.passport.dto.PassportResponse;
import com.boardingpass.be.domain.passport.dto.PassportStampsResponse;
import com.boardingpass.be.domain.passport.dto.PassportVisitDetailResponse;
import com.boardingpass.be.domain.store.VisitLog;
import com.boardingpass.be.domain.store.VisitLogRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import com.boardingpass.be.global.security.SecurityUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassportService {

  private final PassportRepository passportRepository;
  private final PassportStampRepository passportStampRepository;
  private final VisitLogRepository visitLogRepository;
  private final RouteStepRepository routeStepRepository;
  private final CreditLedgerRepository creditLedgerRepository;

  public PassportResponse getPassport() {
    Passport passport = getCurrentPassport();
    return PassportResponse.from(passport);
  }

  public PassportStampsResponse getStamps() {
    Passport passport = getCurrentPassport();
    List<PassportStamp> stamps =
        passportStampRepository.findByPassportIdOrderByCreatedAtDesc(passport.getId());
    return PassportStampsResponse.of(passport, stamps);
  }

  public PassportVisitDetailResponse getVisitDetail(Long visitLogId) {
    Long userId = SecurityUtils.getCurrentUserId();
    VisitLog visitLog = visitLogRepository.findDetailById(visitLogId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.VISIT_LOG_NOT_FOUND));

    if (!visitLog.getUser().getId().equals(userId)) {
      throw new GeneralException(ErrorStatus.FORBIDDEN_VISIT_LOG);
    }

    List<RouteStep> steps = routeStepRepository
        .findByBoardingPassIdOrderBySequenceAsc(visitLog.getBoardingPass().getId());

    return PassportVisitDetailResponse.of(visitLog, steps);
  }

  public PassportCreditsResponse getCredits() {
    Passport passport = getCurrentPassport();
    List<CreditLedger> ledgers =
        creditLedgerRepository.findByUserIdOrderByCreatedAtDesc(passport.getUser().getId());
    return PassportCreditsResponse.of(passport, ledgers);
  }

  private Passport getCurrentPassport() {
    Long userId = SecurityUtils.getCurrentUserId();
    return passportRepository.findByUserId(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.PASSPORT_NOT_FOUND));
  }
}