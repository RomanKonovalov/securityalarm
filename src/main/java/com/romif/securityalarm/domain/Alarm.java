package com.romif.securityalarm.domain;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "alarm")
public class Alarm extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "device_name")
    private String deviceName;

    public Alarm() {
    }

    public Alarm(String deviceName) {
        this.deviceName = deviceName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        if (!deviceName.equals(alarm.deviceName)) return false;
        return super.getCreatedBy().equals(alarm.getCreatedBy());
    }

    @Override
    public int hashCode() {
        int result = deviceName.hashCode();
        result = 31 * result + super.getCreatedBy().hashCode();
        return result;
    }
}
