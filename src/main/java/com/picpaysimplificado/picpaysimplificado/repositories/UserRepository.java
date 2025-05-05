package com.picpaysimplificado.picpaysimplificado.repositories;

import com.picpaysimplificado.picpaysimplificado.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long> {
    // Custom query methods can be defined here if needed
    Optional<User> findUserByDocument(String document);
    Optional<User> findUserById(Long id);
}

