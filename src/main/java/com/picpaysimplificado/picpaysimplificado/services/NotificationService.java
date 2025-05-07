package com.picpaysimplificado.picpaysimplificado.services;

import com.picpaysimplificado.picpaysimplificado.dtos.NotificationDTO;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) throws Exception{
        // URL do serviço de notificação
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        ResponseEntity<String> response = restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, String.class);

        if (!(response.getStatusCode() == HttpStatus.OK)) {
            System.out.println("Erro ao enviar notificacao");
            throw new Exception("Service of notification unavailable");
        }

        //System.out.println("Notificacao enviada para o o usuario");
    }

}
