package com.boardingpass.be.domain.store;

import com.boardingpass.be.domain.boardingpass.BoardingPass;
import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
    name = "visit_log",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_visit_log_boarding_pass",
        columnNames = "boarding_pass_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitLog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "visit_log_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "store_id")
  private Store store;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "boarding_pass_id")
  private BoardingPass boardingPass;

  @Column(name = "entry_no", length = 10, nullable = false)
  private String entryNo;

  @Column(name = "scanned_at", nullable = false)
  private LocalDateTime scannedAt;

  @Column(name = "finished_at")
  private LocalDateTime finishedAt;

  @Column(name = "stay_minutes")
  private Integer stayMinutes;

  public void finish(LocalDateTime finishedAt) {
    this.finishedAt = finishedAt;
    this.stayMinutes = (int) Duration.between(this.scannedAt, finishedAt).toMinutes();
  }
}