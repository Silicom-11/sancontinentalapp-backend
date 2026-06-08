package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeScale {
    @Builder.Default
    private String type = "vigesimal"; // vigesimal, centesimal, literal
    @Builder.Default
    private Integer minGrade = 0;
    @Builder.Default
    private Integer maxGrade = 20;
    @Builder.Default
    private Integer passingGrade = 11;
    private List<LiteralGrade> literalScale;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LiteralGrade {
        private String letter;
        private Integer minScore;
        private Integer maxScore;
        private String description;
    }
}

