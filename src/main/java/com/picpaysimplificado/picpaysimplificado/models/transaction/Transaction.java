package com.picpaysimplificado.picpaysimplificado.models.transaction;

import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.repositories.UserRepository;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "transactions")
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDateTime timestamp;

    public Transaction(BigDecimal amount, User sender, User receiver, TransactionType transactionType) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.type = transactionType;
        this.timestamp = LocalDateTime.now();
    }
}
