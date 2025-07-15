package com.motivation.ietec_cdc.dto;

/**
 * @author EgorBusuioc
 * 08.06.2025
 */

public class ResetPasswordRequest extends  PasswordRequestBase{
    public ResetPasswordRequest(String email) {
        super(email);
    }
}
