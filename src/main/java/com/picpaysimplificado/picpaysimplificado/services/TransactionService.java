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
    private RestTemplate restTemplate;

    public void createTransaction(TransactionDTO transaction) throws Exception {
        // Lógica para criar uma nova transação
        User sender = userService.getUserById(transaction.senderId());
        User receiver = userService.getUserById(transaction.receiverId());

        // Valida a transação
        userService.validateTransaction(sender, transaction.amount());

        // Verifica se o usuário está autorizado a realizar a transação
        boolean isAuthorized = authorizeTransaction(sender, transaction.amount());

        if (!isAuthorized) {
            throw new Exception("Transaction not authorized.");
        }

        // Cria a transação
        Transaction newTransaction = new Transaction();
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setAmount(transaction.amount());
        newTransaction.setTimestamp(LocalDateTime.now());

        // Atualiza o saldo dos usuários
        sender.setBalance(sender.getBalance().subtract(transaction.amount()));
        receiver.setBalance(receiver.getBalance().add(transaction.amount()));

        // Salva a transação
        transactionRepository.save(newTransaction);

        // Salva os usuários atualizados
        userService.saveUser(sender);
        userService.saveUser(receiver);
    }

    public boolean authorizeTransaction (User sender, BigDecimal amount) throws Exception {
        // Lógica
        ResponseEntity<Map> authorizationResponse = restTemplate.postForEntity("https://util.devi.tools/api/v2/authorize", sender, Map.class);

        if (authorizationResponse.getStatusCode() == HttpStatus.OK ) {
            assert authorizationResponse.getBody() != null;
            String message = (String) authorizationResponse.getBody().get("status");
            return "Autorizado".equalsIgnoreCase(message);
        }
        else {
            return false;
        }
    }
}
