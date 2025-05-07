package com.picpaysimplificado.picpaysimplificado.controllers;

import com.picpaysimplificado.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.picpaysimplificado.models.transaction.Transaction;
import com.picpaysimplificado.picpaysimplificado.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDTO transactionDTO) throws Exception {
        // Implement the logic to create a transaction
        Transaction transaction = this.transactionService.createTransfer(transactionDTO);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/deposit/{id}/{amount}")
    public ResponseEntity<Transaction> deposit(@PathVariable Long id, @PathVariable BigDecimal amount) throws Exception {
        Transaction transaction = this.transactionService.deposit(id, amount);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw/{id}/{amount}")
    public ResponseEntity<Transaction> withdraw(@PathVariable Long id, @PathVariable BigDecimal amount) throws Exception {
        Transaction transaction = this.transactionService.withdraw(id, amount);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
}
