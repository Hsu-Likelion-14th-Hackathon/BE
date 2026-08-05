package com.boardingpass.be.domain.survey;

import com.boardingpass.be.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "survey_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyQuestion extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "survey_question_id")
  private Long id;

  @Column(name = "step_no", nullable = false)
  private Integer stepNo;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  // @Builder.Default
  // @OneToMany(mappedBy = "surveyQuestion", fetch = FetchType.LAZY)
  // @OrderBy("orderNo ASC")
  // private List<SurveyOption> options = new ArrayList<>();
}