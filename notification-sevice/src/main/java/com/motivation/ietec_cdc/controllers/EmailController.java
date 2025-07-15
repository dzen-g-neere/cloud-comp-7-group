package com.motivation.ietec_cdc.controllers;

import com.motivation.ietec_cdc.dto.MailRequestCreate;
import com.motivation.ietec_cdc.dto.MailRequestReset;
import com.motivation.ietec_cdc.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author EgorBusuioc
 * 08.06.2025
 */
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/reset-password")
    public ResponseEntity<String> sendResetLink(@RequestBody MailRequestReset mailRequest) {
        try {
            emailService.sendLink(mailRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send reset link: " + e.getMessage());
        }
        return ResponseEntity.ok("Data received successfully.");
    }

    @PostMapping("/create-password")
    public ResponseEntity<String> sendCreationLink(@RequestBody MailRequestCreate mailRequest) {

        try {
            emailService.sendLink(mailRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send reset link: " + e.getMessage());
        }
        return ResponseEntity.ok("Data received successfully.");
    }
}
