package com.iepca.app.model;

import com.iepca.app.model.embedded.AcademicPeriod;
import com.iepca.app.model.embedded.AcademicYearStats;
import com.iepca.app.model.embedded.ImportantDate;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "academicyears")
@CompoundIndexes({
    @CompoundIndex(name = "idx_institution_year", def = "{'institution': 1, 'year': 1}", unique = true)
})
public class AcademicYear {

    @Id
    private String id;

    private String institution; // ref to Institution
    private Integer year;
    private String name;
    private Instant startDate;
    private Instant endDate;

    @Builder.Default
    private List<AcademicPeriod> periods = new ArrayList<>();

    @Builder.Default
    private List<ImportantDate> importantDates = new ArrayList<>();

    @Indexed
    @Builder.Default
    private Boolean isCurrent = false;

    @Builder.Default
    private String status = "planificacion"; // planificacion, matricula, activo, finalizado, cerrado

    @Builder.Default
    private AcademicYearStats stats = new AcademicYearStats();

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public AcademicPeriod getCurrentPeriod() {
        if (periods == null) return null;
        Instant now = Instant.now();
        return periods.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .findFirst()
                .orElse(null);
    }

    public AcademicPeriod getPeriodByNumber(int number) {
        if (periods == null) return null;
        return periods.stream()
                .filter(p -> p.getNumber() != null && p.getNumber() == number)
                .findFirst()
                .orElse(null);
    }
}

