package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {
    @Builder.Default
    private String language = "es";
    @Builder.Default
    private String theme = "light";
    @Builder.Default
    private Boolean notifications = true;
}

