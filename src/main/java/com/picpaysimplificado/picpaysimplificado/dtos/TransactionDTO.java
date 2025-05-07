package com.picpaysimplificado.picpaysimplificado.dtos;

import com.picpaysimplificado.picpaysimplificado.models.transaction.TransactionType;

import java.math.BigDecimal;

public record TransactionDTO(
        BigDecimal amount,
        Long senderId,
        Long receiverId,
        TransactionType type) {
}
