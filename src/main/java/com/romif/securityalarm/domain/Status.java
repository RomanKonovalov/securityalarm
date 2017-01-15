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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceState() {
        return deviceState;
    }

    public Status deviceState(String deviceState) {
        this.deviceState = deviceState;
        return this;
    }

    public void setDeviceState(String deviceState) {
        this.deviceState = deviceState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Status status = (Status) o;
        if (status.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, status.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Status{" +
            "deviceState='" + deviceState + '\'' +
            "} " + super.toString();
    }
}
