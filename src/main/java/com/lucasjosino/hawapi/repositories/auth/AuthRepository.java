package com.lucasjosino.hawapi.repositories.auth;

import com.lucasjosino.hawapi.models.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface that implements the Auth Repository, with JPA CRUD methods.
 *
 * @author Lucas josino
 * @see JpaRepository
 * @see UserModel
 * @since 1.0.0
 */
@Repository
public interface AuthRepository extends JpaRepository<UserModel, UUID> {

    /**
     * Method to get a user by <strong>username</strong> and <strong>email</strong>.
     *
     * @param username An {@link String} that represents the username
     * @param email    An {@link String} that represents the user email
     * @return An {@link Optional} of {@link UserModel}
     * @since 1.0.0
     */
    Optional<UserModel> findByUsernameAndEmail(String username, String email);

    /**
     * Method to check if a user exists using its <strong>username</strong> and <strong>email</strong>.
     *
     * @param username An {@link String} that represents the username
     * @param email    An {@link String} that represents the user email
     * @return true if user exists
     * @since 1.0.0
     */
    boolean existsByUsernameAndEmail(String username, String email);

    /**
     * Method to check if a user exists using its <strong>username</strong>.
     *
     * @param username An {@link String} that represents the username
     * @return true if user exists
     * @since 1.0.0
     */
    boolean existsByUsername(String username);

    /**
     * Method to check if a user exists using its <strong>email</strong>.
     *
     * @param email An {@link String} that represents the user email
     * @return true if user exists
     * @since 1.0.0
     */
    boolean existsByEmail(String email);
}
