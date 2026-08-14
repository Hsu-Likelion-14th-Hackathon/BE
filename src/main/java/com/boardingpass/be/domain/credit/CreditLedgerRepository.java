package com.boardingpass.be.domain.credit;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditLedgerRepository extends JpaRepository<CreditLedger, Long> {

  List<CreditLedger> findByUserIdOrderByCreatedAtDesc(Long userId);

  boolean existsByRefTypeAndRefId(CreditRefType refType, Long refId);
}