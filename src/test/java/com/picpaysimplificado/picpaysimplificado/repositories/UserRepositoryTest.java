package com.picpaysimplificado.picpaysimplificado.repositories;

import com.picpaysimplificado.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.picpaysimplificado.models.user.User;
import com.picpaysimplificado.picpaysimplificado.models.user.UserType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Should get User successfully from DB")
    void findUserByDocument() {
        String document = "12345678900";
        UserDTO data = new UserDTO("John",
                                   "Doe",
                                            document,
                                      "teste@teste.com",
                                   "password",
                                            new BigDecimal(100),
                                            UserType.COMMON
                                  );
        this.createUser(data);
        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isPresent()).isTrue();
    }

    private User createUser(UserDTO data) {
        User user = new User(data);
        this.entityManager.persist(user);
        this.entityManager.flush();
        return user;
    }
}