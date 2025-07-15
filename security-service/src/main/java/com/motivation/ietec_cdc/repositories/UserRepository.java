package com.motivation.ietec_cdc.repositories;

import com.motivation.ietec_cdc.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository
 * @author EgorBusuioc
 * 06.05.2025
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
