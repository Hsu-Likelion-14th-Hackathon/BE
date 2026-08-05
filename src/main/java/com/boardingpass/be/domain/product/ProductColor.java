package com.boardingpass.be.domain.product;

import com.boardingpass.be.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
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
@Table(name = "product_color")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductColor extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_color_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "color_name", length = 50, nullable = false)
  private String colorName;

  @Column(name = "color_hex", length = 7)
  private String colorHex;

  @Builder.Default
  @Column(name = "is_default", nullable = false)
  private boolean isDefault = false;

  // @Builder.Default
  // @OneToMany(mappedBy = "productColor", fetch = FetchType.LAZY)
  // @OrderBy("orderNo ASC")
  // private List<ProductImage> images = new ArrayList<>();

  // @Builder.Default
  // @OneToMany(mappedBy = "productColor", fetch = FetchType.LAZY)
  // @OrderBy("orderNo ASC")
  // private List<ProductSize> sizes = new ArrayList<>();
}