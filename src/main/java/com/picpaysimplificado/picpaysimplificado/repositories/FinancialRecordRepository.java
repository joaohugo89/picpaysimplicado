package com.picpaysimplificado.picpaysimplificado.repositories;

import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialRecord;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository <FinancialRecord, Long> {
    List<FinancialRecord> findByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end);
}
