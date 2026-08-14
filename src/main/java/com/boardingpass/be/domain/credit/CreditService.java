package com.boardingpass.be.domain.credit;

import com.boardingpass.be.domain.passport.Passport;
import com.boardingpass.be.domain.passport.PassportRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreditService {

  private final PassportRepository passportRepository;
  private final CreditLedgerRepository creditLedgerRepository;

  @Transactional
  public int earn(
      Long userId,
      int amount,
      CreditReason reason,
      CreditRefType refType,
      Long refId,
      String description
  ) {
    Passport passport = getPassport(userId);
    passport.applyCredit(amount);

    creditLedgerRepository.save(
        CreditLedger.builder()
            .user(passport.getUser())
            .amount(amount)
            .reason(reason)
            .description(description)
            .refType(refType)
            .refId(refId)
            .balanceAfter(passport.getCreditBalance())
            .build()
    );
    return passport.getCreditBalance();
  }

  @Transactional
  public int spend(
      Long userId,
      int amount,
      CreditReason reason,
      CreditRefType refType,
      Long refId,
      String description
  ) {
    Passport passport = getPassport(userId);

    if (!passport.canAfford(amount)) {
      throw new GeneralException(ErrorStatus.INSUFFICIENT_CREDIT);
    }
    if (refType != null && refId != null
        && creditLedgerRepository.existsByRefTypeAndRefId(refType, refId)) {
      throw new GeneralException(ErrorStatus.DUPLICATE_CREDIT_TRANSACTION);
    }

    passport.applyCredit(-amount);

    creditLedgerRepository.save(
        CreditLedger.builder()
            .user(passport.getUser())
            .amount(-amount)
            .reason(reason)
            .description(description)
            .refType(refType)
            .refId(refId)
            .balanceAfter(passport.getCreditBalance())
            .build()
    );
    return passport.getCreditBalance();
  }

  private Passport getPassport(Long userId) {
    return passportRepository.findByUserId(userId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.PASSPORT_NOT_FOUND));
  }
}