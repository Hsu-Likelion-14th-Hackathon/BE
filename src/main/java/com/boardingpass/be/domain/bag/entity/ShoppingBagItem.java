package com.boardingpass.be.domain.bag.entity;

import com.boardingpass.be.domain.product.ProductSize;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
    name = "shopping_bag_item",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_shopping_bag_user_size",
        columnNames = {"user_id", "product_size_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShoppingBagItem extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shopping_bag_item_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_size_id")
  private ProductSize productSize;

  @Builder.Default
  @Column(name = "quantity", nullable = false)
  private Integer quantity = 1;

  public void addQuantity(int amount) {
    this.quantity += amount;
  }
}