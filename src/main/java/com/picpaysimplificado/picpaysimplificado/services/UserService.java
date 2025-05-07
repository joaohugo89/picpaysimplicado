package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.picpaysimplificado.models.transaction.TransactionType;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.models.user.UserType;
import com.picpaysimplificado.picpaysimplificado.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        // Lógica para buscar todos os usuários
        return this.userRepository.findAll(); // Retorne a lista de usuários
    }

    public void validateTransference(User sender, BigDecimal amount, TransactionType type) throws Exception {
        // Lógica para validar a transação
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Sender does not have enough balance.");
        }
        if (sender.getUserType() == UserType.MERCHANT) {
            throw new Exception("Sender is a merchant and cannot send money.");
        }
        if (type != TransactionType.TRANSFER) {
            throw new Exception("User cannot transfer money between accounts.");
        }
    }

    public void validateTransaction(User user, BigDecimal amount) throws Exception {
        // Lógica para validar o depósito
        if (user.getBalance().compareTo(amount) < 0) {
            throw new Exception("Sender does not have enough balance.");
        }
        if (user.getUserType() == UserType.MERCHANT) {
            throw new Exception("Sender is a merchant and cannot send money.");
        }

    }

    public void refillBalance(Long userId, BigDecimal amount) throws Exception {
        User user = getUserById(userId);
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }

    public void withdrawBalance(Long userId, BigDecimal amount) throws Exception {
        User user = getUserById(userId);
        if (user.getBalance().compareTo(amount) < 0)
            throw new Exception("Insufficient balance");
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
    }

    public User getUserById(Long id) throws Exception{
        // Lógica para buscar um usuário por ID
        return this.userRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
    }

    public void saveUser(User user) {
        // Lógica para criar um novo usuário
        this.userRepository.save(user);
    }

    public User createUser(UserDTO data) {
        User newUser = new User(data);
        this.saveUser(newUser);
        return newUser;
    }
}
