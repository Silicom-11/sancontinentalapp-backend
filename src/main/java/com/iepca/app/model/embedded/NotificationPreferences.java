package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferences {
    @Builder.Default
    private EmailPrefs email = new EmailPrefs();
    @Builder.Default
    private PushPrefs push = new PushPrefs();
    @Builder.Default
    private SmsPrefs sms = new SmsPrefs();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailPrefs {
        @Builder.Default
        private Boolean grades = true;
        @Builder.Default
        private Boolean attendance = true;
        @Builder.Default
        private Boolean events = true;
        @Builder.Default
        private Boolean messages = true;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PushPrefs {
        @Builder.Default
        private Boolean grades = true;
        @Builder.Default
        private Boolean attendance = true;
        @Builder.Default
        private Boolean events = true;
        @Builder.Default
        private Boolean messages = true;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmsPrefs {
        @Builder.Default
        private Boolean emergencies = true;
        @Builder.Default
        private Boolean attendance = false;
    }
}

