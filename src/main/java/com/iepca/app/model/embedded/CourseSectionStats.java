package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionStats {
    @Builder.Default
    private Integer totalStudents = 0;
    @Builder.Default
    private Double averageGrade = 0.0;
    @Builder.Default
    private Double passRate = 0.0;
    @Builder.Default
    private Double attendanceRate = 0.0;
}

