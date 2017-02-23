package com.romif.securityalarm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alarm")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "device", callSuper = false)
public class Alarm extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name="device_id", unique=true, nullable=false, updatable=false)
    private Device device;

    @Column(name = "notification_type")
    @ElementCollection(targetClass = NotificationType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "alarm_notification_type", joinColumns = @JoinColumn(name = "alarm_id"))
    @Enumerated(value = EnumType.STRING)
    private Set<NotificationType> notificationTypes = new HashSet<>();

    @Column(name = "tracking_type")
    @ElementCollection(targetClass = TrackingType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "alarm_tracking_type", joinColumns = @JoinColumn(name = "alarm_id"))
    @Enumerated(value = EnumType.STRING)
    private Set<TrackingType> trackingTypes = new HashSet<>();

    @Column(name = "paused")
    private boolean paused;

}
