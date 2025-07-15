package com.motivation.ietec_cdc.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author EgorBusuioc
 * 09.06.2025
 */
@Setter
@Getter
public class MailRequestCreation extends MailRequestBase {

    public MailRequestCreation(String token, String email) {
        super(token, email);
    }
}
