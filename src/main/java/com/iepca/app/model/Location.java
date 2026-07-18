package com.iepca.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iepca.app.model.embedded.Address;
import com.iepca.app.model.embedded.Coordinates;
import com.iepca.app.model.embedded.DeviceInfo;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "locations")
@CompoundIndexes({
    @CompoundIndex(name = "idx_user_created", def = "{'user': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "idx_user_session", def = "{'user': 1, 'sessionStatus': 1}"),
    @CompoundIndex(name = "idx_coords", def = "{'coordinates.latitude': 1, 'coordinates.longitude': 1}")
})
public class Location {

    @Id
    private String id;

    @Indexed
    private String user; // ref to User

    private Coordinates coordinates;

    @Builder.Default
    private DeviceInfo deviceInfo = new DeviceInfo();

    @Builder.Default
    private String sessionStatus = "online"; // online, offline, background, inactive

    @Builder.Default
    private String updateType = "periodic"; // login, periodic, manual, logout, background, app_open, app_close

    private Address address;

    private Double batteryLevel;

    private Boolean insidePerimeter;

    private Double distanceToSchool;

    @Builder.Default
    private String networkType = "unknown"; // wifi, mobile, none, unknown

    @Builder.Default
    private Instant clientTimestamp = Instant.now();

    @Indexed
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Transient
    private Student student;

    @JsonProperty("_id")
    public String getMongoId() {
        return id;
    }

    @JsonProperty("latitude")
    public Double getLatitude() {
        return coordinates != null ? coordinates.getLatitude() : null;
    }

    @JsonProperty("longitude")
    public Double getLongitude() {
        return coordinates != null ? coordinates.getLongitude() : null;
    }

    @JsonProperty("accuracy")
    public Double getAccuracy() {
        return coordinates != null ? coordinates.getAccuracy() : null;
    }

    @JsonProperty("altitude")
    public Double getAltitude() {
        return coordinates != null ? coordinates.getAltitude() : null;
    }

    @JsonProperty("speed")
    public Double getSpeed() {
        return coordinates != null ? coordinates.getSpeed() : null;
    }

    @JsonProperty("heading")
    public Double getHeading() {
        return coordinates != null ? coordinates.getHeading() : null;
    }

    @JsonProperty("battery")
    public Double getBattery() {
        return batteryLevel != null ? batteryLevel : 0d;
    }

    @JsonProperty("isOnline")
    public boolean isOnline() {
        return "online".equalsIgnoreCase(sessionStatus);
    }

    @JsonProperty("insidePerimeter")
    public Boolean getInsidePerimeter() {
        return insidePerimeter;
    }

    @JsonProperty("distanceToSchool")
    public Double getDistanceToSchool() {
        return distanceToSchool;
    }

    @JsonProperty("timestamp")
    public Instant getTimestamp() {
        return createdAt != null ? createdAt : clientTimestamp;
    }
}

