package com.picpaysimplificado.picpaysimplificado.models.user;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    @Column (unique = true)
    private String document;
    @Column (unique = true)
    private String email;
    private String password;
    private BigDecimal balance;
    private UserType userType;
}
