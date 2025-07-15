package com.motivation.ietec_cdc.dto;

import com.motivation.ietec_cdc.dto.interfaces.MailRequestBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * @author EgorBusuioc
 * 09.06.2025
 */
@AllArgsConstructor
@Getter
@Setter
public class MailRequestCreate implements MailRequestBase {

    private String email;
    private String token;
}
