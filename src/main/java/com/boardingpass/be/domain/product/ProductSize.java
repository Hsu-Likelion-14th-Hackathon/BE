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
    name = "product_size",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_product_size_sku",
        columnNames = "sku"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSize extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_size_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_color_id")
  private ProductColor productColor;

  @Column(name = "size_label", length = 30, nullable = false)
  private String sizeLabel;

  @Column(name = "size_note", length = 30)
  private String sizeNote;

  @Column(name = "sku", length = 50, nullable = false)
  private String sku;

  @Column(name = "stock")
  private Integer stock;

  @Column(name = "order_no", nullable = false)
  private Integer orderNo;

  public boolean hasStock(int quantity) {
    return this.stock != null && this.stock >= quantity;
  }
}