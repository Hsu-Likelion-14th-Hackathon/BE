package com.boardingpass.be.domain.passport;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassportStampRepository extends JpaRepository<PassportStamp, Long> {

  List<PassportStamp> findByPassportIdOrderByCreatedAtDesc(Long passportId);
}