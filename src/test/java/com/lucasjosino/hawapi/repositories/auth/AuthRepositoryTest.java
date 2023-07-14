package com.lucasjosino.hawapi.repositories.auth;

import com.lucasjosino.hawapi.configs.RepositoryUnitTestConfig;
import com.lucasjosino.hawapi.configs.initializer.DatabaseContainerInitializer;
import com.lucasjosino.hawapi.models.user.UserModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryUnitTestConfig
class AuthRepositoryTest extends DatabaseContainerInitializer {

    private UserModel user;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AuthRepository repository;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setUuid(UUID.randomUUID());
        user.setUsername("john_doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("BASIC");
        user.setEmail("johndoe@loremipsum.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setToken("JWT");
        user.setTokenType("Bearer");
        user.setPassword("MY_SUPER_SECRET_PASSWORD");

        entityManager.persist(user);
    }

    @AfterEach
    void tearDown() {
        deleteAndFlushRepository();
    }

    private void deleteAndFlushRepository() {
        entityManager.clear();
        entityManager.flush();
        repository.deleteAll();
    }

    @Test
    void shouldFindUserByUsernameAndEmail() {
        Optional<UserModel> res = repository.findByUsernameAndEmail("john_doe", "johndoe@loremipsum.com");
        assertTrue(res.isPresent());

        UserModel value = res.get();

        assertNotNull(value);
        assertEquals(user.getUsername(), value.getUsername());
        assertEquals(user.getFirstName(), value.getFirstName());
        assertEquals(user.getLastName(), value.getLastName());
        assertEquals(user.getEmail(), value.getEmail());
        assertEquals(user.getRole(), value.getRole());
        assertNotNull(user.getToken());
        assertNotNull(user.getTokenType());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void whenNoUserFoundShouldReturnEmptyOptionalOnFindUserByUsernameAndEmail() {
        Optional<UserModel> res = repository.findByUsernameAndEmail("doe_john", "johndoe@loremipsum.com");
        assertFalse(res.isPresent());
    }

    @Test
    void shouldReturnTrueOnExistsByUsernameAndEmail() {
        boolean res = repository.existsByUsernameAndEmail("john_doe", "johndoe@loremipsum.com");

        assertTrue(res);
    }

    @Test
    void whenNoUsernameFoundShouldReturnFalseOnFindUserByUsernameAndEmail() {
        boolean res = repository.existsByUsernameAndEmail("doe_john", "johndoe@loremipsum.com");

        assertFalse(res);
    }

    @Test
    void shouldReturnTrueOnExistsByUsername() {
        boolean res = repository.existsByUsername("john_doe");

        assertTrue(res);
    }

    @Test
    void whenNoUsernameFoundShouldReturnFalseOnExistsByUsername() {
        boolean res = repository.existsByUsername("doe_john");

        assertFalse(res);
    }

    @Test
    void shouldReturnTrueOnExistsByEmail() {
        boolean res = repository.existsByEmail("johndoe@loremipsum.com");

        assertTrue(res);
    }

    @Test
    void whenNoEmailFoundShouldReturnFalseOnExistsByEmail() {
        boolean res = repository.existsByEmail("doejohn@loremipsum.com");

        assertFalse(res);
    }
}