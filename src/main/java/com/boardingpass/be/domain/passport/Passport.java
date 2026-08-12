package com.boardingpass.be.domain.passport;

import com.boardingpass.be.domain.user.User;
import com.boardingpass.be.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
    name = "passport",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_passport_user", columnNames = "user_id"),
        @UniqueConstraint(name = "uk_passport_no", columnNames = "passport_no")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Passport extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "passport_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "passport_no", length = 10, nullable = false)
  private String passportNo;

  @Builder.Default
  @Column(name = "credit_balance", nullable = false)
  private Integer creditBalance = 0;

  @Builder.Default
  @Column(name = "total_visit_count", nullable = false)
  private Integer totalVisitCount = 0;

  public void assignPassportNo(String passportNo) {
    this.passportNo = passportNo;
  }

  public void applyCredit(int amount) {
    this.creditBalance += amount;
  }

  public void increaseVisitCount() {
    this.totalVisitCount += 1;
  }

  public boolean canAfford(int cost) {
    return this.creditBalance >= cost;
  }
}