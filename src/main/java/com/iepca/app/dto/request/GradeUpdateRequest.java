package com.iepca.app.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeUpdateRequest {

    @NotBlank(message = "El curso es obligatorio")
    private String courseId;

    @NotNull(message = "El bimestre es obligatorio")
    @Min(1) @Max(4)
    private Integer bimester;

    @NotNull(message = "Las calificaciones son obligatorias")
    private List<GradeEntry> grades;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradeEntry {
        @NotBlank(message = "El estudiante es obligatorio")
        private String studentId;

        @NotBlank(message = "La evaluaciÃ³n es obligatoria")
        private String evaluationId;

        @Min(0) @Max(20)
        private Double score;

        private String comments;
    }
}

