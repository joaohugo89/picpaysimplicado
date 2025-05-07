package com.picpaysimplificado.picpaysimplificado.controllers;

import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialGoal;
import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialRecord;
import com.picpaysimplificado.picpaysimplificado.models.financiation.FinanciationType;
import com.picpaysimplificado.picpaysimplificado.services.FinancialGoalService;
import com.picpaysimplificado.picpaysimplificado.services.FinancialRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/financial")
public class FinancialController {

    @Autowired
    private FinancialRecordService financialRecordService;

    @Autowired
    private FinancialGoalService financialGoalService;

    // Financial Record endpoints
    @PostMapping("/records/{userId}")
    public FinancialRecord createRecord(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) FinanciationType type) throws Exception {
        return financialRecordService.addFinancialRecord(userId, amount, description, category, type);
    }

    @GetMapping("/records/{userId}")
    public List<FinancialRecord> getRecordsByMonth(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) throws Exception {
        return financialRecordService.getRecordsByUserAndMonth(userId, month, year);
    }

    // Financial Goal endpoints
    @PostMapping("/goals/{userId}")
    public FinancialGoal createGoal(
            @PathVariable Long userId,
            @RequestParam BigDecimal targetAmount,
            @RequestParam String description,
            @RequestParam(required = false) String deadline) throws Exception {
        LocalDate parsedDeadline = deadline != null ? LocalDate.parse(deadline) : null;
        return financialGoalService.createGoal(userId, targetAmount, description, parsedDeadline);
    }

    @GetMapping("/goals/{userId}")
    public List<FinancialGoal> getGoals(@PathVariable Long userId) throws Exception {
        return financialGoalService.getGoalsByUser(userId);
    }

    @PutMapping("/goals/{goalId}/update")
    public FinancialGoal updateGoalProgress(
            @PathVariable Long goalId,
            @RequestParam BigDecimal contribution) throws Exception {
        return financialGoalService.updateGoalProgress(goalId, contribution);
    }
}

