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
public class AcademicPeriod {
    private String name;
    private Integer number;
    private Instant startDate;
    private Instant endDate;
    @Builder.Default
    private Boolean isActive = false;
    @Builder.Default
    private Boolean gradesLocked = false;
    @Builder.Default
    private String status = "pendiente"; // pendiente, activo, finalizado
}

