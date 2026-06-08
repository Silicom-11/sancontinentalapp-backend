package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String street;
    private String district;
    private String city;
    private String region;
    private String postalCode;
    private String reference;
    private String country;
    private String formattedAddress;
}

