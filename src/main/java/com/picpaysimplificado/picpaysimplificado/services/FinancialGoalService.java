package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialGoal;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.repositories.FinancialGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialGoalService {

    @Autowired
    private FinancialGoalRepository financialGoalRepository;

    @Autowired
    private UserService userService;

    public FinancialGoal createGoal(Long userId, BigDecimal targetAmount, String description, LocalDate deadline) throws Exception {
        User user = userService.getUserById(userId);
        FinancialGoal goal = new FinancialGoal();
        goal.setUser(user);
        goal.setTargetAmount(targetAmount);
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setDescription(description);
        goal.setDeadline(deadline);
        goal.setAchieved(false);
        return financialGoalRepository.save(goal);
    }

    public FinancialGoal updateGoalProgress(Long goalId, BigDecimal amount) throws Exception {
        FinancialGoal goal = financialGoalRepository.findById(goalId)
                .orElseThrow(() -> new Exception("Goal not found"));

        BigDecimal updated = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(updated);

        if (updated.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setAchieved(true);
        }

        return financialGoalRepository.save(goal);
    }

    public List<FinancialGoal> getGoalsByUser(Long userId) throws Exception {
        User user = userService.getUserById(userId);
        return financialGoalRepository.findByUser(user);
    }
}
