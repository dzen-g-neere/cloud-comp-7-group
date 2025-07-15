package com.motivation.ietec_cdc.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author EgorBusuioc
 * 23.06.2025
 */
@Service
public class AesService {

    private final SecretKey secretKey;
    private final IvParameterSpec iv;

    public AesService(@Value("${aes.password}") String password) throws Exception {
        this.secretKey = AESKeyUtil.getKeyFromPassword(password);
        this.iv = AESIVUtil.getFixedIV();
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decryptedBytes);
    }

    public static class AESIVUtil {
        public static IvParameterSpec getFixedIV() {
            byte[] iv = "1234567890abcdef".getBytes();
            return new IvParameterSpec(Arrays.copyOf(iv, 16));
        }
    }

    public static class AESKeyUtil {
        public static SecretKey getKeyFromPassword(String password) throws Exception {
            byte[] key = password.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        }
    }
}
