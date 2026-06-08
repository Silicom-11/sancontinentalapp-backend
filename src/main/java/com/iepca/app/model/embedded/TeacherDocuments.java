package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDocuments {
    private String dniCopy;
    private String cv;
    private String degree;
    private String criminalRecord;
    private String healthCertificate;
}

