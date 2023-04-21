package com.lucasjosino.hawapi.repositories.auth;

import com.lucasjosino.hawapi.models.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<UserModel, UUID> {

    Optional<UserModel> findByUsernameAndEmail(String username, String email);

    boolean existsByUsernameAndEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
