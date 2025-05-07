package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.models.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthorizationService authorizationService;

    private User sender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sender = new User();
        sender.setId(1L);
        sender.setEmail("john@example.com");
    }

    @Test
    @DisplayName("Should return true when authorization is approved")
    void testAuthorizeTransactionApproved() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("authorization", true);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", data);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        boolean result = authorizationService.authorizeTransaction(sender, new BigDecimal("100.00"));

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when authorization is denied")
    void testAuthorizeTransactionDenied() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("authorization", false);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", data);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        boolean result = authorizationService.authorizeTransaction(sender, new BigDecimal("100.00"));

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when API returns unexpected format")
    void testAuthorizeTransactionUnexpectedResponse() throws Exception {
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        boolean result = authorizationService.authorizeTransaction(sender, new BigDecimal("100.00"));

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when API throws exception")
    void testAuthorizeTransactionWithException() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenThrow(new RuntimeException("API unavailable"));

        boolean result = authorizationService.authorizeTransaction(sender, new BigDecimal("100.00"));

        assertFalse(result);
    }
}
