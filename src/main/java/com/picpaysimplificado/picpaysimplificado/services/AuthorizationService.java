package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AuthorizationService {

    @Autowired
    private RestTemplate restTemplate;

    public boolean authorizeTransaction(User sender, BigDecimal amount) throws Exception {
        try {
            System.out.println("Sending authorization request to external service...");
            ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity(
                    "https://util.devi.tools/api/v2/authorize", Map.class);


            System.out.println("Authorization HTTP status: " + authorizationResponse.getStatusCode());

            if (authorizationResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = authorizationResponse.getBody();
                System.out.println("Authorization response body: " + body);

                if (body == null) {
                    System.err.println("❌ Response body is null.");
                    return false;
                }

                Object dataObj = body.get("data");
                if (!(dataObj instanceof Map)) {
                    System.err.println("❌ 'data' field is not a valid map: " + dataObj);
                    return false;
                }

                Map<String, Object> data = (Map<String, Object>) dataObj;
                Object auth = data.get("authorization");

                System.out.println("Authorization result: " + auth);
                return Boolean.TRUE.equals(auth);
            } else {
                System.err.println("❌ Unexpected HTTP status: " + authorizationResponse.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ Exception during authorization: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
