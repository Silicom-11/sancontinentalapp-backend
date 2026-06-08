package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalInfo {
    private String bloodType;
    private String allergies;
    private String conditions;
    private String medications;
    private String emergencyContact;
    private String emergencyPhone;
    private String insuranceProvider;
    private String insuranceNumber;
}

