package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialRecord;
import com.picpaysimplificado.picpaysimplificado.models.financiation.FinanciationType;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.repositories.FinancialRecordRepository;
import com.picpaysimplificado.picpaysimplificado.utils.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialRecordServiceTest {

    @Mock
    private FinancialRecordRepository financialRecordRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FinancialRecordService financialRecordService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
    }

    @Test
    @DisplayName("Should add a financial record successfully")
    void testAddFinancialRecord() throws Exception {
        BigDecimal amount = new BigDecimal("100.00");
        String category = "Food";
        String description = "Lunch";
        FinanciationType type = FinanciationType.EXPENSE;

        FinancialRecord mockRecord = new FinancialRecord();
        mockRecord.setUser(user);
        mockRecord.setAmount(amount);
        mockRecord.setCategory(category);
        mockRecord.setDescription(description);
        mockRecord.setType(type);
        mockRecord.setDate(LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(user);
        when(financialRecordRepository.save(any(FinancialRecord.class))).thenReturn(mockRecord);

        FinancialRecord result = financialRecordService.addFinancialRecord(1L, amount, category, description, type);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(amount, result.getAmount());
        assertEquals(category, result.getCategory());
        assertEquals(description, result.getDescription());
        assertEquals(type, result.getType());
    }

    @Test
    @DisplayName("Should return financial records for a user by month")
    void testGetRecordsByUserAndMonth() throws Exception {
        int month = 5;
        int year = 2025;

        LocalDateTime startOfMonth = DateTimeUtils.getStartOfMonth(year, month);
        LocalDateTime endOfMonth = DateTimeUtils.getEndOfMonth(year, month);

        FinancialRecord record1 = new FinancialRecord();
        FinancialRecord record2 = new FinancialRecord();
        List<FinancialRecord> records = Arrays.asList(record1, record2);

        when(userService.getUserById(1L)).thenReturn(user);
        when(financialRecordRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth)).thenReturn(records);

        List<FinancialRecord> result = financialRecordService.getRecordsByUserAndMonth(1L, month, year);

        assertEquals(2, result.size());
        verify(financialRecordRepository).findByUserAndDateBetween(user, startOfMonth, endOfMonth);
    }
}
