package com.boardingpass.be.domain.survey;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "survey_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyOption extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "survey_option_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "survey_question_id")
  private SurveyQuestion surveyQuestion;

  @Column(name = "label", length = 50, nullable = false)
  private String label;

  @Column(name = "description", length = 200)
  private String description;

  @Column(name = "tag", length = 30, nullable = false)
  private String tag;

  @Column(name = "order_no", nullable = false)
  private Integer orderNo;
}