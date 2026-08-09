package com.boardingpass.be.domain.boardingpass;

import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
    name = "boarding_pass",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_boarding_pass_code",
        columnNames = "pass_code"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardingPass extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "boarding_pass_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "pass_code", length = 64, nullable = false)
  private String passCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private BoardingPassStatus status;

  @Builder.Default
  @Column(name = "data_consent", nullable = false)
  private Boolean dataConsent = false;

  @Builder.Default
  @OneToMany(mappedBy = "boardingPass", fetch = FetchType.LAZY)
  private List<BoardingPassItem> items = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "boardingPass", fetch = FetchType.LAZY)
  private List<BoardingPassSurvey> surveys = new ArrayList<>();

  public boolean isIssued() {
    return this.status == BoardingPassStatus.ISSUED;
  }

  public boolean isScanned() {
    return this.status == BoardingPassStatus.SCANNED;
  }

  public void markScanned() {
    this.status = BoardingPassStatus.SCANNED;
  }

  public void markCompleted() {
    this.status = BoardingPassStatus.COMPLETED;
  }
}