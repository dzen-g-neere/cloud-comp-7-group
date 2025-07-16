package com.motivation.ietec_cdc.services;

import com.motivation.ietec_cdc.dto.CreatePasswordRequest;
import com.motivation.ietec_cdc.dto.ExternalUser;
import com.motivation.ietec_cdc.dto.LoginRequest;
import com.motivation.ietec_cdc.dto.Maker;
import com.motivation.ietec_cdc.models.User;
import com.motivation.ietec_cdc.models.enums.Role;
import com.motivation.ietec_cdc.repositories.UserRepository;
import com.motivation.ietec_cdc.security.JWTUtils;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * Service responsible for handling user authentication and registration logic.
 *
 * @author EgorBusuioc
 * @since 06.05.2025
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;
    private final AesService aesService;
    private final JWTUtils jwtUtils;

    /**
     * Registers a new user in the system.
     * <p>
     * The method first checks if the email is already in use. If not, it sets the user's
     * role to {@code ROLE_USER}, encrypts the password, and persists the user to the database.
     * </p>
     *
     * @param user the user object to be registered
     * @throws IllegalArgumentException\ if a user with the same email already exists
     */
    @Transactional
    public void registerNewUser(User user) throws Exception {
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email should have a valid format - mail@gmail.com");
        }

        if (userRepository.findByEmail(aesService.encrypt(user.getEmail())).isPresent())
            throw new IllegalArgumentException("A user with this email already exists.");

        try {
            user.setEmail(aesService.encrypt(user.getEmail()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }

        if (user.getRole() == null)
            user.setRole(Role.ROLE_CREATOR); // Set the user as CREATOR by default

        if (user.getUserId() == null) {
            ULID ulid = new ULID();
            user.setUserId(ulid.nextValue().toString());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encoding the password
        user.setCreationDate(LocalDateTime.now());

        userRepository.save(user); // Saving the user into the database
        log.info("User created: Email: {}", user.getEmail());
    }

    @Transactional
    public void registerNewPatientAccount(CreatePasswordRequest user, String creatorId, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Id", creatorId); // Set the creator ID in the header
        headers.set("X-API-KEY", "SuperSecretApiWord");

        HttpEntity<ExternalUser> entity = new HttpEntity<>(new ExternalUser(userId,
                Integer.valueOf(user.getPatientCode()),
                user.getFirstName(),
                user.getLastName()),
                headers);

        restTemplate.postForEntity("http://data-service:8084/management/map-maker-user", entity, String.class);
        log.info("New patient account created: User ID: {}, Patient Code: {}", userId, user.getPatientCode());
    }

    /**
     * Authenticates an existing user using their email and password.
     * <p>
     * The method uses the {@code AuthenticationManager} to authenticate the user. If successful,
     * it generates a JWT token for the user and returns it.
     * </p>
     *
     * @param loginRequest the login request containing email and password
     * @return a JWT token if authentication is successful
     * @throws IllegalArgumentException if authentication fails
     */
    public String loginUser(LoginRequest loginRequest) {
        try {
            loginRequest.setEmail(aesService.encrypt(loginRequest.getEmail()));
            User user = (User) authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            ).getPrincipal();
            user.setEmail(aesService.decrypt(loginRequest.getEmail()));
            log.info("Authentication successful for user: {}", loginRequest.getEmail());
            log.info("User have been found: Email: {}", loginRequest.getEmail());
            log.info("JWT token generating...");
            return jwtUtils.generateToken(user);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    public User findExistingUserByEmail(String email) {

        log.info("Trying to find user by email: {}", email);
        User user = userRepository.findByEmail(email).orElse(null);
        log.info("User has been found.");
        return user;
    }

    public void updateUser(User user) {
        userRepository.save(user);
        log.info("User updated successfully: {}", user.getEmail());
    }
}
