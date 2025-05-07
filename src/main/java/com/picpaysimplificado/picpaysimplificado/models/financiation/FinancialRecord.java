package com.picpaysimplificado.picpaysimplificado.models.financiation;

import com.picpaysimplificado.picpaysimplificado.models.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "financial_records")
@Table(name = "financial_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class FinancialRecord {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private BigDecimal amount;
    private String category; // ex: "Alimentação", "Transporte"
    private String description;
    private FinanciationType type;

    private LocalDateTime date;
}
