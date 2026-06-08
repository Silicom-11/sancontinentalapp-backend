package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodEvaluation {
    private Integer period; // 1-4
    private List<PeriodEvaluationItem> evaluations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodEvaluationItem {
        private String name;
        private String type; // examen, tarea, proyecto, participacion, practica, exposicion
        private Double weight;
        @Builder.Default
        private Double maxGrade = 20.0;
        private Instant date;
    }
}

