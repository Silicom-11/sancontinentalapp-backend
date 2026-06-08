package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportantDate {
    private String name;
    private Instant date;
    private String type; // inicio_clases, fin_clases, vacaciones, feriado, examen, evento
}

