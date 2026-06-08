package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guardian {
    private String student; // ObjectId as String reference
    private String relationship; // padre, madre, tutor, abuelo, etc.
    @Builder.Default
    private Boolean isPrimaryContact = false;
    @Builder.Default
    private Boolean canPickUp = true;
    @Builder.Default
    private Boolean isEmergencyContact = true;
}

