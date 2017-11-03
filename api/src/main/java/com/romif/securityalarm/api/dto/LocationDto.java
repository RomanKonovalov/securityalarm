package com.romif.securityalarm.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class LocationDto {

    private double timestamp;

    private double timestampError;

    private double latitude;

    private double longitude;

    private double altitude;

    private double latitudeError;

    private double longitudeError;

    private double altitudeError;

    private double course;

    private double speed;

    private double climbRate;

    private double courseError;

    private double speedError;

    private double climbRateError;

    public LocationDto(double timestamp, double timestampError, double latitude, double longitude, double altitude, double latitudeError, double longitudeError, double altitudeError, double course, double speed, double climbRate, double courseError, double speedError, double climbRateError) {
        this.timestamp = timestamp;
        this.timestampError = timestampError;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.latitudeError = latitudeError;
        this.longitudeError = longitudeError;
        this.altitudeError = altitudeError;
        this.course = course;
        this.speed = speed;
        this.climbRate = climbRate;
        this.courseError = courseError;
        this.speedError = speedError;
        this.climbRateError = climbRateError;
    }
}
