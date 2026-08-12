package com.boardingpass.be.domain.passport.dto;

import com.boardingpass.be.domain.credit.CreditLedger;
import com.boardingpass.be.domain.credit.CreditReason;
import java.time.LocalDateTime;

public record CreditHistoryResponse(
    Long creditLedgerId,
    Integer amount,
    CreditReason reason,
    String description,
    Integer balanceAfter,
    LocalDateTime createdAt
) {
  public static CreditHistoryResponse from(CreditLedger ledger) {
    return new CreditHistoryResponse(
        ledger.getId(),
        ledger.getAmount(),
        ledger.getReason(),
        ledger.getDescription(),
        ledger.getBalanceAfter(),
        ledger.getCreatedAt()
    );
  }
}