package com.boardingpass.be.domain.fitting;

import com.boardingpass.be.domain.product.ProductColor;
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
@Table(name = "fitting_session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FittingSession extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "fitting_session_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_color_id")
  private ProductColor productColor;

  @Column(name = "source_image_url", columnDefinition = "TEXT", nullable = false)
  private String sourceImageUrl;

  @Column(name = "result_image_url", columnDefinition = "TEXT")
  private String resultImageUrl;

  @Column(name = "credit_cost", nullable = false)
  private Integer creditCost;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private FittingStatus status;

  public void complete(String resultImageUrl) {
    this.resultImageUrl = resultImageUrl;
    this.status = FittingStatus.DONE;
  }

  public void fail() {
    this.status = FittingStatus.FAILED;
  }
}