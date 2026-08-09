package com.boardingpass.be.domain.boardingpass.dto;

import com.boardingpass.be.domain.boardingpass.BoardingPass;
import com.boardingpass.be.domain.boardingpass.BoardingPassItem;
import com.boardingpass.be.domain.boardingpass.BoardingPassStatus;
import java.time.LocalDateTime;
import java.util.List;

public record BoardingPassIssueResponse(
    Long boardingPassId,
    String passCode,
    BoardingPassStatus status,
    String passengerName,
    LocalDateTime issuedAt,
    List<BoardingPassItemResponse> items
) {
  public static BoardingPassIssueResponse of(BoardingPass pass, List<BoardingPassItem> items) {
    return new BoardingPassIssueResponse(
        pass.getId(),
        pass.getPassCode(),
        pass.getStatus(),
        pass.getUser().getName(),
        pass.getCreatedAt(),
        items.stream().map(BoardingPassItemResponse::from).toList()
    );
  }
}