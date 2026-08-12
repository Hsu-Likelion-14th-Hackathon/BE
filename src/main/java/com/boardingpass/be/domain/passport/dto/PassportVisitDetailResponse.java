package com.boardingpass.be.domain.passport.dto;

import com.boardingpass.be.domain.boardingpass.RouteStep;
import com.boardingpass.be.domain.store.VisitLog;
import java.time.LocalDateTime;
import java.util.List;

public record PassportVisitDetailResponse(
    Long visitLogId,
    String storeName,
    String address,
    String entryNo,
    LocalDateTime scannedAt,
    Integer stayMinutes,
    VisitBoardingPassResponse boardingPass,
    List<TravelHistoryResponse> travelHistory
) {
  public static PassportVisitDetailResponse of(VisitLog visitLog, List<RouteStep> steps) {
    return new PassportVisitDetailResponse(
        visitLog.getId(),
        visitLog.getStore().getName(),
        visitLog.getStore().getAddress(),
        visitLog.getEntryNo(),
        visitLog.getScannedAt(),
        visitLog.getStayMinutes(),
        VisitBoardingPassResponse.from(visitLog.getBoardingPass()),
        steps.stream().map(TravelHistoryResponse::from).toList()
    );
  }
}