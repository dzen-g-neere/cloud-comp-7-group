package com.motivation.ietec_cdc.repositories;

import com.motivation.ietec_cdc.models.ResetPasswordToken;
import com.motivation.ietec_cdc.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author EgorBusuioc
 * 08.06.2025
 */
public interface PasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByUser(User user);
    Optional<ResetPasswordToken> findByToken(String token);
}
