package com.picpaysimplificado.picpaysimplificado.controllers;

import com.picpaysimplificado.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.models.user.UserType;
import com.picpaysimplificado.picpaysimplificado.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController();
        userController.userService = userService; // Manually inject mock
    }

    @Test
    @DisplayName("Should create a user")
    void testCreateUser() {
        UserDTO userDTO = new UserDTO("John", "Doe", "12345678900", "john@example.com", "password", new BigDecimal("100.00"), UserType.COMMON);
        User user = new User(userDTO);
        user.setId(1L);

        when(userService.createUser(userDTO)).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(userDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());
        assertEquals(user.getFirstname(), response.getBody().getFirstname());
        assertEquals(user.getEmail(), response.getBody().getEmail());

        verify(userService).createUser(userDTO);
    }

    @Test
    @DisplayName("Should get all users")
    void testGetAllUsers() {
        User user1 = new User(new UserDTO("John", "Doe", "12345678900", "john@example.com", "pass", new BigDecimal("100.00"), UserType.COMMON));
        user1.setId(1L);
        User user2 = new User(new UserDTO("Jane", "Smith", "98765432100", "jane@example.com", "pass", new BigDecimal("200.00"), UserType.MERCHANT));
        user2.setId(2L);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstname());
        assertEquals("Jane", response.getBody().get(1).getFirstname());

        verify(userService).getAllUsers();
    }
}
