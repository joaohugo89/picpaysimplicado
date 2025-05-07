package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.models.user.UserType;
import com.picpaysimplificado.picpaysimplificado.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User commonUser;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("John",
                              "Doe",
                              "12345678900",
                                 "john@example.com",
                              "password",
                                       new BigDecimal("100.00"),
                                       UserType.COMMON);
        commonUser = new User(userDTO);
        commonUser.setId(1L);
    }

    @Test
    @DisplayName("Should return all users")
    void getAllUsers_shouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(commonUser));
        List<User> users = userService.getAllUsers();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getDocument()).isEqualTo("12345678900");
    }

    @Test
    @DisplayName("Should validate transaction successfully")
    void validateTranference_shouldPass() {
        assertThatCode(() -> userService.validateTransaction(commonUser, new BigDecimal("50.00")))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw exception when balance is insufficient")
    void validateTranference_insufficientBalance_shouldThrow() {
        assertThatThrownBy(() -> userService.validateTransaction(commonUser, new BigDecimal("150.00")))
                .isInstanceOf(Exception.class)
                .hasMessage("Sender does not have enough balance.");
    }

    @Test
    @DisplayName("Should throw exception if sender is merchant")
    void validateTranference_merchant_shouldThrow() {
        commonUser.setUserType(UserType.MERCHANT);
        assertThatThrownBy(() -> userService.validateTransaction(commonUser, new BigDecimal("10.00")))
                .isInstanceOf(Exception.class)
                .hasMessage("Sender is a merchant and cannot send money.");
    }

    @Test
    @DisplayName("Should return user by ID")
    void getUserById_shouldReturnUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));
        User result = userService.getUserById(1L);
        assertThat(result).isEqualTo(commonUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getUserById_notFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(Exception.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("Should save user")
    void saveUser_shouldCallRepository() {
        userService.saveUser(commonUser);
        verify(userRepository).save(commonUser);
    }

    @Test
    @DisplayName("Should create user from DTO")
    void createUser_shouldCreateAndSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(commonUser);
        User result = userService.createUser(userDTO);
        verify(userRepository).save(any(User.class));
        assertThat(result.getDocument()).isEqualTo("12345678900");
    }

    @Test
    @DisplayName("Should refill user balance")
    void refillBalance() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));
        userService.refillBalance(1L, new BigDecimal("50.00"));
        assertThat(commonUser.getBalance()).isEqualTo(new BigDecimal("150.00"));
        verify(userRepository).save(commonUser);
    }

    @Test
    @DisplayName("Should throw exception when refilling balance fails")
    void withdrawBalance() {
        commonUser.setBalance(new BigDecimal("200.00"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(commonUser));
        assertThatCode(() -> userService.withdrawBalance(1L, new BigDecimal("50.00")))
                .doesNotThrowAnyException();
        assertThat(commonUser.getBalance()).isEqualTo(new BigDecimal("150.00"));
        verify(userRepository).save(commonUser);
    }
}
