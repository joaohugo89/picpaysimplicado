package com.picpaysimplificado.picpaysimplificado.controllers;

import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.picpaysimplificado.models.transaction.Transaction;
import com.picpaysimplificado.picpaysimplificado.models.transaction.TransactionType;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.models.user.UserType;
import com.picpaysimplificado.picpaysimplificado.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UserDTO senderDTO = new UserDTO(
                "John", "Doe", "12345678900", "john@example.com", "password",
                new BigDecimal("100.00"), UserType.COMMON);
        sender = new User(senderDTO);
        sender.setId(1L);

        UserDTO receiverDTO = new UserDTO(
                "Jane", "Smith", "98765432100", "jane@example.com", "password",
                new BigDecimal("50.00"), UserType.COMMON);
        receiver = new User(receiverDTO);
        receiver.setId(2L);
    }

    @Test
    @DisplayName("Should create a transaction")
    void testCreateTransaction() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO(
                new BigDecimal("20"), sender.getId(), receiver.getId(), TransactionType.TRANSFER);
        Transaction expectedTransaction = new Transaction(
                new BigDecimal("20"), sender, receiver, TransactionType.TRANSFER);

        when(transactionService.createTransfer(transactionDTO)).thenReturn(expectedTransaction);

        ResponseEntity<Transaction> response = transactionController.createTransaction(transactionDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("20"), response.getBody().getAmount());
        assertEquals(TransactionType.TRANSFER, response.getBody().getType());

        verify(transactionService).createTransfer(transactionDTO);
    }

    @Test
    @DisplayName("Should deposit an amount")
    void testDeposit() throws Exception {
        BigDecimal depositAmount = new BigDecimal("50");
        Transaction expectedTransaction = new Transaction(depositAmount, sender, null, TransactionType.DEPOSIT);

        when(transactionService.deposit(sender.getId(), depositAmount)).thenReturn(expectedTransaction);

        ResponseEntity<Transaction> response = transactionController.deposit(sender.getId(), depositAmount);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(depositAmount, response.getBody().getAmount());
        assertEquals(TransactionType.DEPOSIT, response.getBody().getType());

        verify(transactionService).deposit(sender.getId(), depositAmount);
    }

    @Test
    @DisplayName("Should withdraw an amount")
    void testWithdraw() throws Exception {
        BigDecimal withdrawAmount = new BigDecimal("30");
        Transaction expectedTransaction = new Transaction(withdrawAmount, sender, null, TransactionType.WITHDRAWAL);

        when(transactionService.withdraw(sender.getId(), withdrawAmount)).thenReturn(expectedTransaction);

        ResponseEntity<Transaction> response = transactionController.withdraw(sender.getId(), withdrawAmount);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(withdrawAmount, response.getBody().getAmount());
        assertEquals(TransactionType.WITHDRAWAL, response.getBody().getType());

        verify(transactionService).withdraw(sender.getId(), withdrawAmount);
    }
}
