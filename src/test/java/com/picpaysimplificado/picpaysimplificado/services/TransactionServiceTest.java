package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.models.transaction.TransactionType;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.models.user.UserType;
import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.picpaysimplificado.repositories.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthorizationService authorizationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        // Initialize mocks and any other setup needed before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create transaction successfully")
    void createTransactionSuccess() throws Exception {
        User sender = new User(1L, "John", "Doe", "12345678900","john@email.com",
                "password", new BigDecimal(100), UserType.COMMON);
        User receiver = new User(2L, "Jane", "Doe", "09876543210","jane@email.com",
                "password", new BigDecimal(50), UserType.COMMON);

        when(userService.getUserById(1L)).thenReturn(sender);
        when(userService.getUserById(2L)).thenReturn(receiver);

        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(20), 1L, 2L , TransactionType.TRANSFER);
        transactionService.createTransfer(request);
        verify(transactionRepository, times(1)).save(any());

        sender.setBalance(new BigDecimal(90));
        verify(userService, times(1)).saveUser(sender);
        receiver.setBalance(new BigDecimal(70));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transfer of " + request.amount() + " to " + receiver.getFirstname());
        verify(notificationService, times(1)).sendNotification(receiver, "Transfer of " + request.amount() + " from " + sender.getFirstname());


    }

    @Test
    @DisplayName("Should throw Exception when Transaction is not allowed")
    void createTransactionCase2() throws Exception {
        User sender = new User(1L, "Maria", "Souza", "99999999901", "maria@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);
        User receiver = new User(2L, "Joao", "Souza", "99999999902", "joao@gmail.com", "12345", new BigDecimal(10), UserType.COMMON);

        when(userService.getUserById(1L)).thenReturn(sender);
        when(userService.getUserById(2L)).thenReturn(receiver);

        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(false);

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L, TransactionType.TRANSFER);
            transactionService.createTransfer(request);
        });

        Assertions.assertEquals("Transaction not authorized.", thrown.getMessage());
    }
}