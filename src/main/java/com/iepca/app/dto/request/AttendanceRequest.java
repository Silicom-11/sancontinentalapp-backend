package com.iepca.app.dto.request;

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
public class AttendanceRequest {

    @NotBlank(message = "El curso es obligatorio")
    private String courseId;

    @NotNull(message = "La fecha es obligatoria")
    private String date;

    @NotNull(message = "Los registros de asistencia son obligatorios")
    private List<AttendanceEntry> records;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceEntry {
        @NotBlank(message = "El estudiante es obligatorio")
        private String studentId;

        @NotBlank(message = "El estado es obligatorio")
        private String status; // present, absent, late, justified

        private String arrivalTime;
        private String observations;
    }
}

