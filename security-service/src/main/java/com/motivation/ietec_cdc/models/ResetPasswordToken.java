package com.motivation.ietec_cdc.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author EgorBusuioc
 * 08.06.2025
 */
@Entity
@Table(name = "reset_password")
@Data
@NoArgsConstructor
public class ResetPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reset_password_id")
    private Long passwordId;

    @Column(name = "token")
    private String token;

    @OneToOne(mappedBy = "resetPasswordToken")
    private User user;

    @Column(name = "expiration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expirationDate;

    public ResetPasswordToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    @PrePersist
    public void setExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusHours(6); // Token valid for 6 hour
    }
}
