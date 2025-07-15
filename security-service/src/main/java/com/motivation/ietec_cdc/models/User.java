package com.motivation.ietec_cdc.models;

import com.motivation.ietec_cdc.models.enums.Role;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User model
 * @author EgorBusuioc
 * 06.05.2025
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "user_id")
    private String userId;

    @NotBlank(message = "Email should not be empty")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Password should not be empty")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter and one digit"
    )
    @Column(name = "password")
    private String password;

    @Transient
    private String companyName;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime creationDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "reset_password_id")
    private ResetPasswordToken resetPasswordToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
