package com.motivation.ietec_cdc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * @author EgorBusuioc
 * 09.06.2025
 */
@Getter
@Setter
public class ResetPassword {
    String token;
    @NotBlank(message = "Password should not be empty")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter and one digit")
    String password;
}
