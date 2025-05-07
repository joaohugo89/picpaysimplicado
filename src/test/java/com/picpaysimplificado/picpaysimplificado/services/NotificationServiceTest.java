package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.dtos.NotificationDTO;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.models.user.UserType;
import com.picpaysimplificado.picpaysimplificado.dtos.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationService notificationService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserDTO userDTO = new UserDTO("Alice", "Wonderland", "11122233344", "alice@example.com", "pass123",
                new BigDecimal("100.00"), UserType.COMMON);
        user = new User(userDTO);
        user.setId(1L);
    }

    @Test
    @DisplayName("Should send notification successfully")
    void testSendNotificationSuccess() throws Exception {
        String message = "Test notification";
        NotificationDTO expectedRequest = new NotificationDTO(user.getEmail(), message);
        String url = "https://util.devi.tools/api/v1/notify";

        // Simulate success
        when(restTemplate.postForEntity(eq(url), any(NotificationDTO.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        // Call method
        notificationService.sendNotification(user, message);

        // Verify restTemplate was called
        verify(restTemplate).postForEntity(eq(url), eq(expectedRequest), eq(String.class));
    }

    @Test
    @DisplayName("Should throw exception when notification fails")
    void testSendNotificationFailure() {
        String message = "Fail notification";
        String url = "https://util.devi.tools/api/v1/notify";

        when(restTemplate.postForEntity(eq(url), any(NotificationDTO.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));

        Exception exception = assertThrows(Exception.class, () ->
                notificationService.sendNotification(user, message)
        );

        assertEquals("Service of notification unavailable", exception.getMessage());
        verify(restTemplate).postForEntity(eq(url), any(NotificationDTO.class), eq(String.class));
    }
}
