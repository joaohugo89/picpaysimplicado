package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.models.financiation.FinancialRecord;
import com.picpaysimplificado.picpaysimplificado.models.financiation.FinanciationType;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.repositories.FinancialRecordRepository;
import com.picpaysimplificado.picpaysimplificado.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinancialRecordService {
    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    @Autowired
    private UserService userService;

    public FinancialRecord addFinancialRecord(Long userId, BigDecimal amount, String category, String description, FinanciationType type) throws Exception {
        User user = userService.getUserById(userId);
        FinancialRecord record = new FinancialRecord();
        record.setUser(user);
        record.setAmount(amount);
        record.setCategory(category);
        record.setDescription(description);
        record.setType(type);
        record.setDate(LocalDateTime.now());
        return financialRecordRepository.save(record);
    }

    public List<FinancialRecord> getRecordsByUserAndMonth(Long userId, int month, int year) throws Exception {
        User user = userService.getUserById(userId);

        LocalDateTime startOfMonth = DateTimeUtils.getStartOfMonth(year, month);
        LocalDateTime endOfMonth = DateTimeUtils.getEndOfMonth(year, month);

        return financialRecordRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth);
    }

}
