package com.picpaysimplificado.picpaysimplificado.repositories;

import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialGoal;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    // Custom query methods can be defined here if needed
    List<FinancialGoal> findByUser(User user);
}
