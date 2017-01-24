package com.romif.securityalarm.service.dto;

import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.NotificationType;
import com.romif.securityalarm.domain.TrackingType;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

public class AlarmDTO {

    private Long id;

    private Set<NotificationType> notificationTypes;

    private Set<TrackingType> trackingTypes;

    private ZonedDateTime createdDate;

    public AlarmDTO() {
    }

    public AlarmDTO(Alarm alarm) {
        this.id = alarm.getId();
        this.notificationTypes = alarm.getNotificationTypes();
        this.trackingTypes = alarm.getTrackingTypes();
        this.createdDate = alarm.getCreatedDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<NotificationType> getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(Set<NotificationType> notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    public Set<TrackingType> getTrackingTypes() {
        return trackingTypes;
    }

    public void setTrackingTypes(Set<TrackingType> trackingTypes) {
        this.trackingTypes = trackingTypes;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
