package com.motivation.ietec_cdc.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.motivation.ietec_cdc.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for generating JWT tokens
 * @author EgorBusuioc
 * 07.05.2025
 */
@Component
@Slf4j
public class JWTUtils {

    private final Integer EXPIRATION_TIME = 30;

    /**
     * Takes the private key from the resources folder and loads it
     * @throws Exception if the key cannot be loaded
     */
    private static RSAPrivateKey loadPrivateKey() throws Exception {

        ClassPathResource resource = new ClassPathResource("private.pem");
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    /**
     * Takes the public key from the resources folder and loads it
     * @throws Exception if the key cannot be loaded
     */
    public static RSAPublicKey loadPublicKey() throws Exception {
        ClassPathResource resource = new ClassPathResource("public.pem");
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    /**
     * Generates a JWT token for the given user
     * @param user the user to generate the token for
     * @return the generated token
     */
    public String generateToken(User user) {

        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(EXPIRATION_TIME).toInstant());
        RSAPrivateKey privateKey;
        try {
             privateKey = loadPrivateKey();
        } catch (Exception e){
            log.error("Error loading private key", e);
            return null;
        }

        return JWT.create()
                .withSubject("User details")
                .withClaim("user-id", user.getUserId())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(new Date())
                .withIssuer("IETEC-SECURITY")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.RSA256(null, privateKey));
    }
}
