package com.romif.securityalarm.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Status.
 */
@Entity
@Table(name = "status")
public class Status extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "device_state")
    private String deviceState;

    @Column(name = "latitude")
    private float latitude;

    @Column(name = "longitude")
    private float longitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(String deviceState) {
        this.deviceState = deviceState;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        return id != null ? id.equals(status.id) : status.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Status{" +
            "id=" + id +
            ", deviceState='" + deviceState + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", createdDate=" + super.getCreatedDate() +
            ", createdBy=" + super.getCreatedBy();
    }
}
