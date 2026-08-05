package com.boardingpass.be.domain.user;

import com.boardingpass.be.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_users_provider",
        columnNames = {"provider", "provider_uid"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(name = "name", length = 50)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider", length = 20, nullable = false)
  private Provider provider;

  @Column(name = "provider_uid", length = 100, nullable = false)
  private String providerUid;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "nationality", length = 50)
  private String nationality;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "default_body_image_url", columnDefinition = "TEXT")
  private String defaultBodyImageUrl;

  public void completeProfile(String name, LocalDate birthDate, String nationality) {
    this.name = name;
    this.birthDate = birthDate;
    this.nationality = nationality;
  }

  public void updateDefaultBodyImage(String imageUrl) {
    this.defaultBodyImageUrl = imageUrl;
  }

  public void clearDefaultBodyImage() {
    this.defaultBodyImageUrl = null;
  }

  public boolean hasDefaultBodyImage() {
    return this.defaultBodyImageUrl != null && !this.defaultBodyImageUrl.isBlank();
  }
}