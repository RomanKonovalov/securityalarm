package com.romif.securityalarm.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "device_credentials")
public class DeviceCredentials implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name="device_id")
    private Device device;

    @Column(name = "raw_password")
    private String rawPassword;

    @Column(name = "token")
    private String token;

    public DeviceCredentials() {
    }

    public DeviceCredentials(Device device, String rawPassword, String token) {
        this.device = device;
        this.rawPassword = rawPassword;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceCredentials that = (DeviceCredentials) o;

        return device != null ? device.equals(that.device) : that.device == null;
    }

    @Override
    public int hashCode() {
        return device != null ? device.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeviceCredentials{" +
            "device=" + device +
            ", rawPassword='" + rawPassword + '\'' +
            ", token=" + token +
            '}';
    }
}
