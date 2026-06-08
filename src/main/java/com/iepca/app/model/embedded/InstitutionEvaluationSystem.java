package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionEvaluationSystem {
    @Builder.Default
    private String type = "bimestral"; // bimestral, trimestral, semestral, anual
    @Builder.Default
    private Integer periodsPerYear = 4;
    @Builder.Default
    private List<String> periodNames = List.of("I Bimestre", "II Bimestre", "III Bimestre", "IV Bimestre");
}

