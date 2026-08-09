package com.boardingpass.be.domain.boardingpass;

import com.boardingpass.be.domain.survey.SurveyOption;
import com.boardingpass.be.domain.survey.SurveyQuestion;
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
import jakarta.persistence.UniqueConstraint;

@Entity
@Getter
@Builder
@Table(
  name = "boarding_pass_survey",
  uniqueConstraints = @UniqueConstraint(
      name = "uk_boarding_pass_survey_question",
      columnNames = {"boarding_pass_id", "survey_question_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardingPassSurvey extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "boarding_pass_survey_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "boarding_pass_id")
  private BoardingPass boardingPass;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "survey_question_id")
  private SurveyQuestion surveyQuestion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "survey_option_id")
  private SurveyOption surveyOption;

  @Column(name = "text_answer", length = 200)
  private String textAnswer;
}