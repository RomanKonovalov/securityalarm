package com.romif.securityalarm.service.dto;

/**
 * Created by Roman_Konovalov on 1/19/2017.
 */
public class LocationDTO {

    private float latitude;

    private float longitude;

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public LocationDTO() {
    }

    public LocationDTO(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
