package com.boardingpass.be.domain.credit;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "credit_ledger")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditLedger extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "credit_ledger_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "amount", nullable = false)
  private Integer amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "reason", length = 20, nullable = false)
  private CreditReason reason;

  @Column(name = "description", length = 100)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "ref_type", length = 30)
  private CreditRefType refType;

  @Column(name = "ref_id")
  private Long refId;

  @Column(name = "balance_after", nullable = false)
  private Integer balanceAfter;
}