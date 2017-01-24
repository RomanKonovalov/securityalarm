package com.romif.securityalarm.domain;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "alarm")
public class Alarm extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "notification_type")
    @ElementCollection(targetClass = NotificationType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "alarm_notification_type", joinColumns = @JoinColumn(name = "alarm_id"))
    @Enumerated(value = EnumType.STRING)
    private Set<NotificationType> notificationTypes;

    @Column(name = "tracking_type")
    @ElementCollection(targetClass = TrackingType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "alarm_tracking_type", joinColumns = @JoinColumn(name = "alarm_id"))
    @Enumerated(value = EnumType.STRING)
    private Set<TrackingType> trackingTypes;

    public Alarm() {
    }

    public Alarm(String deviceName, EnumSet<NotificationType> notificationTypes, EnumSet<TrackingType> trackingTypes) {
        this.deviceName = deviceName;
        this.notificationTypes = notificationTypes;
        this.trackingTypes = trackingTypes;
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

}
