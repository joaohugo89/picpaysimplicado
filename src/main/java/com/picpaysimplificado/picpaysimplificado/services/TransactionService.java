package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.picpaysimplificado.models.transaction.Transaction;
import com.picpaysimplificado.picpaysimplificado.models.transaction.TransactionType;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthorizationService authorizationService;

    public Transaction createTransfer(TransactionDTO transaction) throws Exception {
        User sender = userService.getUserById(transaction.senderId());
        User receiver = userService.getUserById(transaction.receiverId());

        userService.validateTransference(sender, transaction.amount(), transaction.type());

        boolean isAuthorized = authorizationService.authorizeTransaction(sender, transaction.amount());
        if (!isAuthorized ) {
            throw new Exception("Transaction not authorized.");
        }

        sender.setBalance(sender.getBalance().subtract(transaction.amount()));
        receiver.setBalance(receiver.getBalance().add(transaction.amount()));

        Transaction newTransaction = new Transaction();
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setAmount(transaction.amount());
        newTransaction.setTimestamp(LocalDateTime.now());
        newTransaction.setType(TransactionType.TRANSFER);

        transactionRepository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);

        notificationService.sendNotification(sender, "Transfer of " + transaction.amount() + " to " + receiver.getFirstname());
        notificationService.sendNotification(receiver, "Transfer of " + transaction.amount() + " from " + sender.getFirstname());

        return newTransaction;
    }

    public Transaction deposit(Long userId, BigDecimal amount) throws Exception {
        User user = userService.getUserById(userId);
        user.setBalance(user.getBalance().add(amount));

        Transaction transaction = new Transaction();
        transaction.setReceiver(user);
        transaction.setSender(user);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setType(TransactionType.DEPOSIT);

        userService.saveUser(user);
        transactionRepository.save(transaction);

        notificationService.sendNotification(user, "Deposit of " + amount);
        return transaction;
    }

    public Transaction withdraw(Long userId, BigDecimal amount) throws Exception {
        User user = userService.getUserById(userId);
        if (user.getBalance().compareTo(amount) < 0) {
            throw new Exception("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(amount));

        Transaction transaction = new Transaction();
        transaction.setSender(user);
        transaction.setReceiver(user);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setType(TransactionType.WITHDRAWAL);

        userService.saveUser(user);
        transactionRepository.save(transaction);

        notificationService.sendNotification(user, "Withdrawal of " + amount);
        return transaction;
    }
}
