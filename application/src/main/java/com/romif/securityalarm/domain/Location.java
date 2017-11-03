package com.romif.securityalarm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode
@Entity
@Table(name = "location")
@Data
@ToString
@NoArgsConstructor
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

}
