package com.boardingpass.be.domain.passport;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PassportStampRepository extends JpaRepository<PassportStamp, Long> {
}