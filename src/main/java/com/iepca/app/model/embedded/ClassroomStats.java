package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomStats {
    @Builder.Default
    private Integer enrolledStudents = 0;
    @Builder.Default
    private Integer maleStudents = 0;
    @Builder.Default
    private Integer femaleStudents = 0;
}

