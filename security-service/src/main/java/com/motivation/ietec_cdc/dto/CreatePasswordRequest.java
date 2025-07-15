package com.motivation.ietec_cdc.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author EgorBusuioc
 * 09.06.2025
 */
@Getter
@Setter
public class CreatePasswordRequest extends PasswordRequestBase{

    private String patientCode;
    private String firstName;
    private String lastName;

    public CreatePasswordRequest(String email, String patientCode, String firstName, String lastName) {
        super(email);
        this.patientCode = patientCode;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
