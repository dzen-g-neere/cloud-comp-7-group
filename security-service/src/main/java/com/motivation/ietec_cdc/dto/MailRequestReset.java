package com.motivation.ietec_cdc.dto;

/**
 * @author EgorBusuioc
 * 08.06.2025
 */

public class MailRequestReset extends MailRequestBase {
    public MailRequestReset(String token, String email) {
        super(token, email);
    }
}
