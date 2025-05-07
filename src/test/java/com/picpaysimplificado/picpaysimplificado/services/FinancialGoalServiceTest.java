package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialGoal;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.repositories.FinancialGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialGoalServiceTest {

    @Mock
    private FinancialGoalRepository financialGoalRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FinancialGoalService financialGoalService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
    }

    @Test
    @DisplayName("Should create a financial goal successfully")
    void testCreateGoal() throws Exception {
        BigDecimal targetAmount = new BigDecimal("500");
        String description = "Vacation";
        LocalDate deadline = LocalDate.now().plusMonths(6);

        FinancialGoal goal = new FinancialGoal();
        goal.setUser(user);
        goal.setTargetAmount(targetAmount);
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setDescription(description);
        goal.setDeadline(deadline);
        goal.setAchieved(false);

        when(userService.getUserById(1L)).thenReturn(user);
        when(financialGoalRepository.save(any(FinancialGoal.class))).thenReturn(goal);

        FinancialGoal result = financialGoalService.createGoal(1L, targetAmount, description, deadline);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(targetAmount, result.getTargetAmount());
        assertEquals(BigDecimal.ZERO, result.getCurrentAmount());
        assertFalse(result.isAchieved());
    }

    @Test
    @DisplayName("Should update goal progress and mark as achieved if met")
    void testUpdateGoalProgress() throws Exception {
        FinancialGoal goal = new FinancialGoal();
        goal.setId(1L);
        goal.setTargetAmount(new BigDecimal("100"));
        goal.setCurrentAmount(new BigDecimal("90"));
        goal.setAchieved(false);

        when(financialGoalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(financialGoalRepository.save(any(FinancialGoal.class))).thenAnswer(i -> i.getArgument(0));

        FinancialGoal result = financialGoalService.updateGoalProgress(1L, new BigDecimal("15"));

        assertEquals(new BigDecimal("105"), result.getCurrentAmount());
        assertTrue(result.isAchieved());
    }

    @Test
    @DisplayName("Should return list of goals for a user")
    void testGetGoalsByUser() throws Exception {
        FinancialGoal goal1 = new FinancialGoal();
        FinancialGoal goal2 = new FinancialGoal();

        when(userService.getUserById(1L)).thenReturn(user);
        when(financialGoalRepository.findByUser(user)).thenReturn(Arrays.asList(goal1, goal2));

        List<FinancialGoal> result = financialGoalService.getGoalsByUser(1L);

        assertEquals(2, result.size());
        verify(financialGoalRepository).findByUser(user);
    }
}
