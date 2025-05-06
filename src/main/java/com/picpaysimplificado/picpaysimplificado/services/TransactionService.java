package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.picpaysimplificado.models.transaction.Transaction;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RestTemplate restTemplate;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        System.out.println("=== Starting transaction process ===");
        System.out.println("Sender ID: " + transaction.senderId());
        System.out.println("Receiver ID: " + transaction.receiverId());
        System.out.println("Amount: " + transaction.amount());

        // Step 1: Get Users
        User sender = this.userService.getUserById(transaction.senderId());
        User receiver = this.userService.getUserById(transaction.receiverId());

        System.out.println("Sender found: " + sender.getFirstname() + " | Balance: " + sender.getBalance());
        System.out.println("Receiver found: " + receiver.getFirstname() + " | Balance: " + receiver.getBalance());

        // Step 2: Validate transaction
        System.out.println("Validating transaction...");
        userService.validateTransaction(sender, transaction.amount());
        System.out.println("Transaction validated.");

        // Step 3: Authorization
        System.out.println("Authorizing transaction...");
        boolean isAuthorized = this.authorizeTransaction(sender, transaction.amount());

        if (!isAuthorized) {
            System.err.println("❌ Transaction not authorized.");
            throw new Exception("Transaction not authorized.");
        }
        System.out.println("✅ Transaction authorized.");

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

        System.out.println("Updated sender balance: " + sender.getBalance());
        System.out.println("Updated receiver balance: " + receiver.getBalance());

        // Step 6: Save transaction and users
        transactionRepository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);
        System.out.println("Transaction and users saved.");

        // Step 7: Send notifications
        notificationService.sendNotification(sender, "Transaction of " + transaction.amount() + " to " + receiver.getFirstname());
        notificationService.sendNotification(receiver, "Transaction of " + transaction.amount() + " from " + sender.getFirstname());
        System.out.println("Notifications sent.");

        System.out.println("=== Transaction process completed ===");
        return newTransaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal amount) throws Exception {
        try {
            System.out.println("Sending authorization request to external service...");
            ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity(
                    "https://util.devi.tools/api/v2/authorize", Map.class);


            System.out.println("Authorization HTTP status: " + authorizationResponse.getStatusCode());

            if (authorizationResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = authorizationResponse.getBody();
                System.out.println("Authorization response body: " + body);

                if (body == null) {
                    System.err.println("❌ Response body is null.");
                    return false;
                }

                Object dataObj = body.get("data");
                if (!(dataObj instanceof Map)) {
                    System.err.println("❌ 'data' field is not a valid map: " + dataObj);
                    return false;
                }

                Map<String, Object> data = (Map<String, Object>) dataObj;
                Object auth = data.get("authorization");

                System.out.println("Authorization result: " + auth);
                return Boolean.TRUE.equals(auth);
            } else {
                System.err.println("❌ Unexpected HTTP status: " + authorizationResponse.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception during authorization: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
