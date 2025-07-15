package com.motivation.ietec_cdc.dto;

import com.motivation.ietec_cdc.dto.interfaces.MailRequestBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author EgorBusuioc
 * 08.06.2025
 */

@AllArgsConstructor
@Setter
@Getter
public class MailRequestReset implements MailRequestBase {
    private String token;
    private String email;
}
