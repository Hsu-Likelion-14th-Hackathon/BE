package com.boardingpass.be.domain.passport.dto;

import com.boardingpass.be.domain.credit.CreditLedger;
import com.boardingpass.be.domain.passport.Passport;
import java.util.List;

public record PassportCreditsResponse(
    Integer creditBalance,
    List<CreditHistoryResponse> histories
) {
  public static PassportCreditsResponse of(Passport passport, List<CreditLedger> ledgers) {
    return new PassportCreditsResponse(
        passport.getCreditBalance(),
        ledgers.stream().map(CreditHistoryResponse::from).toList()
    );
  }
}