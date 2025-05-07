package com.picpaysimplificado.picpaysimplificado.controllers;

import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialGoal;
import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialRecord;
import com.picpaysimplificado.picpaysimplificado.models.financiation.FinanciationType;
import com.picpaysimplificado.picpaysimplificado.models.transaction.TransactionType;
import com.picpaysimplificado.picpaysimplificado.services.FinancialGoalService;
import com.picpaysimplificado.picpaysimplificado.services.FinancialRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialControllerTest {

    @Mock
    private FinancialRecordService financialRecordService;

    @Mock
    private FinancialGoalService financialGoalService;

    @InjectMocks
    private FinancialController financialController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRecord() throws Exception {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(new BigDecimal("100.00"));

        when(financialRecordService.addFinancialRecord(1L, new BigDecimal("100.00"), "Test desc", "Food", FinanciationType.EXPENSE))
                .thenReturn(record);

        FinancialRecord result = financialController.createRecord(1L, new BigDecimal("100.00"), "Test desc", "Food", FinanciationType.EXPENSE);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
    }

    @Test
    void testGetRecordsByMonth() throws Exception {
        FinancialRecord record = new FinancialRecord();
        record.setDescription("Test");

        when(financialRecordService.getRecordsByUserAndMonth(1L, 5, 2025)).thenReturn(List.of(record));

        List<FinancialRecord> results = financialController.getRecordsByMonth(1L, 5, 2025);

        assertEquals(1, results.size());
        assertEquals("Test", results.get(0).getDescription());
    }

    @Test
    void testCreateGoal() throws Exception {
        FinancialGoal goal = new FinancialGoal();
        goal.setTargetAmount(new BigDecimal("500.00"));

        when(financialGoalService.createGoal(1L, new BigDecimal("500.00"), "Save money", LocalDate.parse("2025-12-31")))
                .thenReturn(goal);

        FinancialGoal result = financialController.createGoal(1L, new BigDecimal("500.00"), "Save money", "2025-12-31");

        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getTargetAmount());
    }

    @Test
    void testGetGoals() throws Exception {
        FinancialGoal goal = new FinancialGoal();
        goal.setDescription("Goal Test");

        when(financialGoalService.getGoalsByUser(1L)).thenReturn(List.of(goal));

        List<FinancialGoal> goals = financialController.getGoals(1L);

        assertEquals(1, goals.size());
        assertEquals("Goal Test", goals.get(0).getDescription());
    }

    @Test
    void testUpdateGoalProgress() throws Exception {
        FinancialGoal goal = new FinancialGoal();
        goal.setCurrentAmount(new BigDecimal("200.00"));

        when(financialGoalService.updateGoalProgress(1L, new BigDecimal("100.00"))).thenReturn(goal);

        FinancialGoal result = financialController.updateGoalProgress(1L, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("200.00"), result.getCurrentAmount());
    }
}
