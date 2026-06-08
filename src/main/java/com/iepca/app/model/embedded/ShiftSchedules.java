package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftSchedules {
    @Builder.Default
    private ShiftTime morning = ShiftTime.builder().startTime("07:30").endTime("13:00").build();
    @Builder.Default
    private ShiftTime afternoon = ShiftTime.builder().startTime("13:00").endTime("18:30").build();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShiftTime {
        @Builder.Default
        private String startTime = "07:30";
        @Builder.Default
        private String endTime = "13:00";
    }
}

