package com.romif.securityalarm.service.dto;

import com.romif.securityalarm.domain.NotificationType;
import com.romif.securityalarm.domain.TrackingType;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
public class AlarmDTO {

    private Long id;

    private Set<NotificationType> notificationTypes;

    private Set<TrackingType> trackingTypes;

    private ZonedDateTime createdDate;

    private boolean paused;

}
