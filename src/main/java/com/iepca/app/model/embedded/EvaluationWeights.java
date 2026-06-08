package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationWeights {
    @Builder.Default
    private Integer examen = 40;
    @Builder.Default
    private Integer tarea = 20;
    @Builder.Default
    private Integer proyecto = 20;
    @Builder.Default
    private Integer participacion = 10;
    @Builder.Default
    private Integer practica = 10;
    // Legacy fields (from Course model)
    private Integer exams;
    private Integer tasks;
    private Integer projects;
}

