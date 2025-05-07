package com.picpaysimplificado.picpaysimplificado.models.financiation;

import com.picpaysimplificado.picpaysimplificado.models.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "financial_goals")
@Table(name = "financial_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class FinancialGoal {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String description;

    private LocalDate deadline;

    private boolean achieved;
}
