package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicLevels {
    @Builder.Default
    private LevelConfig initial = LevelConfig.builder().enabled(false).grades(0).build();
    @Builder.Default
    private LevelRange primary = LevelRange.builder().enabled(true).from(1).to(6).build();
    @Builder.Default
    private LevelRange secondary = LevelRange.builder().enabled(true).from(1).to(5).build();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelConfig {
        @Builder.Default
        private Boolean enabled = false;
        @Builder.Default
        private Integer grades = 0;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelRange {
        @Builder.Default
        private Boolean enabled = true;
        @Builder.Default
        private Integer from = 1;
        @Builder.Default
        private Integer to = 6;
    }
}

