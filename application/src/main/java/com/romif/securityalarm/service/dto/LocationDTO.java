package com.romif.securityalarm.service.dto;

import lombok.Data;

@Data
public class LocationDTO {

    private double latitude;

    private double longitude;

    public LocationDTO() {
    }

    public LocationDTO(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
