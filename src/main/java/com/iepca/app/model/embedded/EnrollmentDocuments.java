package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDocuments {
    private String enrollmentForm;
    private String paymentReceipt;
    private String commitmentLetter;
    private String previousReport;
}

