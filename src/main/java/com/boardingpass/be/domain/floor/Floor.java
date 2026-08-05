package com.boardingpass.be.domain.floor;

import com.boardingpass.be.domain.store.Store;
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
@Table(name = "floor")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Floor extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "floor_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "store_id")
  private Store store;

  @Column(name = "floor_no", nullable = false)
  private Integer floorNo;

  @Column(name = "code", length = 20, nullable = false)
  private String code;

  @Column(name = "title", length = 50, nullable = false)
  private String title;

  @Column(name = "tagline", length = 200)
  private String tagline;

  @Column(name = "audio_url", columnDefinition = "TEXT")
  private String audioUrl;

  // @Builder.Default
  // @OneToMany(mappedBy = "floor", fetch = FetchType.LAZY)
  // @OrderBy("orderNo ASC")
  // private List<FloorContent> contents = new ArrayList<>();
}