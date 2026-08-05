package com.boardingpass.be.domain.floor;

import com.boardingpass.be.domain.product.Product;
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
@Table(name = "floor_content")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FloorContent extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "floor_content_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "floor_id")
  private Floor floor;

  @Column(name = "order_no", nullable = false)
  private Integer orderNo;

  @Enumerated(EnumType.STRING)
  @Column(name = "block_type", length = 20, nullable = false)
  private BlockType blockType;

  @Column(name = "body", columnDefinition = "TEXT")
  private String body;

  @Column(name = "image_url", columnDefinition = "TEXT")
  private String imageUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;
}