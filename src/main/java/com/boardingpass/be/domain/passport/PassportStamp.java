package com.boardingpass.be.domain.passport;

import com.boardingpass.be.domain.store.VisitLog;
import com.boardingpass.be.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    name = "passport_stamp",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_passport_stamp_visit_log",
        columnNames = "visit_log_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PassportStamp extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "passport_stamp_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "passport_id")
  private Passport passport;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "visit_log_id")
  private VisitLog visitLog;

  @Column(name = "stamp_asset_url", columnDefinition = "TEXT")
  private String stampAssetUrl;
}