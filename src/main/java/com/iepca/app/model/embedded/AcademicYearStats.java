package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearStats {
    @Builder.Default
    private Integer totalStudents = 0;
    @Builder.Default
    private Integer totalTeachers = 0;
    @Builder.Default
    private Integer totalClassrooms = 0;
    @Builder.Default
    private Integer totalCourses = 0;
}

