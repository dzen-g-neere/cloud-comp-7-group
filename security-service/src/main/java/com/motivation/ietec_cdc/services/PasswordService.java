package com.motivation.ietec_cdc.services;

import com.motivation.ietec_cdc.dto.*;
import com.motivation.ietec_cdc.models.ResetPasswordToken;
import com.motivation.ietec_cdc.models.User;
import com.motivation.ietec_cdc.models.enums.Role;
import com.motivation.ietec_cdc.repositories.PasswordTokenRepository;
import com.motivation.ietec_cdc.repositories.UserRepository;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.UUID;


/**
 * @author EgorBusuioc
 * 08.06.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PasswordService {

    private static final String MAIL_SERVICE_URL = "http://notification-service:8085";

    private final AuthService authService;
    private final PasswordTokenRepository passwordTokenRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AesService aesService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int DEFAULT_LENGTH = 20;
    private static final SecureRandom random = new SecureRandom();

    public void sendLink(PasswordRequestBase passwordRequestBase, String creatorId) throws Exception {
        User user;

        if (passwordRequestBase instanceof ResetPasswordRequest) {
            user = handleResetRequest((ResetPasswordRequest) passwordRequestBase);
        } else if (passwordRequestBase instanceof CreatePasswordRequest) {
            user = handleCreateRequest((CreatePasswordRequest) passwordRequestBase, creatorId);
        } else {
            throw new IllegalArgumentException("Unsupported request type: " + passwordRequestBase.getClass().getSimpleName());
        }

        String token = UUID.randomUUID().toString();
        log.info("Token is generated.");
        createPasswordResetTokenForUser(user, token);
        log.info("Token has been saved in the database for user: {}", user.getEmail());

        if (passwordRequestBase instanceof ResetPasswordRequest) {
            sendEmail(new MailRequestReset(token, aesService.decrypt(user.getEmail())), "/reset-password");
        } else {
            sendEmail(new MailRequestCreation(token, aesService.decrypt(user.getEmail())), "/create-password");
        }
    }

    public void setNewPassword(ResetPassword resetPassword) {
        final ResetPasswordToken passwordToken = passwordTokenRepository.findByToken(resetPassword.getToken())
                .orElseThrow(() -> new UsernameNotFoundException("Token not found"));

        User user = passwordToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
        user.setResetPasswordToken(null);
        authService.updateUser(user);
        log.info("Password has been changed for user: {}", user.getEmail());

        passwordTokenRepository.delete(passwordToken);
        log.info("Reset password token has been deleted for user: {}", user.getEmail());
    }

    private User handleResetRequest(ResetPasswordRequest request) throws Exception {
        User user = authService.findExistingUserByEmail(aesService.encrypt(request.getEmail()));

        if (user == null) {
            log.error("User with email {} not found", request.getEmail());
            throw new UsernameNotFoundException("User with this email does not exist");
        }

        if (passwordTokenRepository.findByUser(user).isPresent()) {
            log.error("User with email {} already has a reset password token", request.getEmail());
            throw new IllegalStateException("User already has a reset password token.");
        }

        return user;
    }

    private User handleCreateRequest(CreatePasswordRequest request, String creatorId) throws Exception{
        if (request.getPatientCode().length() != 9) {
            log.warn("Patient code must be a 9-digit number");
            throw new IllegalArgumentException("Patient code must be a 9-digit number");
        }

        User user = authService.findExistingUserByEmail(aesService.encrypt(request.getEmail()));

        if (user != null) {
            log.error("User with email {} already exists", user.getEmail());
            throw new UsernameNotFoundException("User with this email already exists");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setRole(Role.ROLE_USER);

        ULID ulid = new ULID();
        newUser.setUserId(ulid.nextValue().toString());
        newUser.setPassword(generateTemporaryPassword(DEFAULT_LENGTH));

        authService.registerNewUser(newUser);
        authService.registerNewPatientAccount(request, creatorId, newUser.getUserId());

        return newUser;
    }

    private void sendEmail(MailRequestBase mailRequest, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MailRequestBase> request = new HttpEntity<>(mailRequest, headers);
        restTemplate.postForEntity(MAIL_SERVICE_URL + endpoint, request, String.class);
        log.info("Email with reset link sent to: {}", mailRequest.getEmail());
    }

    private void createPasswordResetTokenForUser(User user, String token) {
        ResetPasswordToken myToken = new ResetPasswordToken(token, user);
        user.setResetPasswordToken(myToken);
        passwordTokenRepository.save(myToken);
        userRepository.save(user);
    }

    public static String generateTemporaryPassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
