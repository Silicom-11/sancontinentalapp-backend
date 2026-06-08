package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDocuments {
    private String dniCopy;
    private String birthCertificate;
    private String enrollmentForm;
    private String photo;
    private String medicalCertificate;
    private String previousReport;
}

