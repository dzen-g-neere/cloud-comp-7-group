package com.motivation.ietec_cdc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author EgorBusuioc
 * 10.07.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalUser {
    private String patientId;
    private Integer patientInsuranceCode;
    private String patientName;
    private String patientSurname;
}
