package com.motivation.ietec_cdc.services;

import com.motivation.ietec_cdc.models.ResetPasswordToken;
import com.motivation.ietec_cdc.models.User;
import com.motivation.ietec_cdc.repositories.PasswordTokenRepository;
import com.motivation.ietec_cdc.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author EgorBusuioc
 * 09.06.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AutoDeleteResetTokensService {

    private final PasswordTokenRepository passwordTokenRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredTokens() {
        List<ResetPasswordToken> tokens = passwordTokenRepository.findAll();

        for (ResetPasswordToken token : tokens) {
            if (token.getExpirationDate().isBefore(LocalDateTime.now())) {

                User user = token.getUser();
                user.setResetPasswordToken(null);
                userRepository.save(user);
                passwordTokenRepository.delete(token);
                log.info("Deleted expired token: {}", token.getToken());
            }
        }
    }
}
