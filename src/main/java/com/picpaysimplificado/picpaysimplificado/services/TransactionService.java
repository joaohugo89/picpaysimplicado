package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.picpaysimplificado.models.transaction.Transaction;
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

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        // Step 1: Get Users
        User sender = this.userService.getUserById(transaction.senderId());
        User receiver = this.userService.getUserById(transaction.receiverId());

        // Step 2: Validate transaction
        userService.validateTransaction(sender, transaction.amount());

        // Step 3: Authorization
        boolean isAuthorized = this.authorizationService.authorizeTransaction(sender, transaction.amount());

        if (!isAuthorized) {
            throw new Exception("Transaction not authorized.");
        }

        // Step 4: Create transaction object
        Transaction newTransaction = new Transaction();
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setAmount(transaction.amount());
        newTransaction.setTimestamp(LocalDateTime.now());

        // Step 5: Update balances
        BigDecimal senderOldBalance = sender.getBalance();
        BigDecimal receiverOldBalance = receiver.getBalance();

        sender.setBalance(senderOldBalance.subtract(transaction.amount()));
        receiver.setBalance(receiverOldBalance.add(transaction.amount()));

        // Step 6: Save transaction and users
        transactionRepository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);

        // Step 7: Send notifications
        notificationService.sendNotification(sender, "Transaction of " + transaction.amount() + " to " + receiver.getFirstname());
        notificationService.sendNotification(receiver, "Transaction of " + transaction.amount() + " from " + sender.getFirstname());
        return newTransaction;
    }
}
