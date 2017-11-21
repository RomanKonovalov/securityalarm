package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.api.config.AuthoritiesConstants;
import com.romif.securityalarm.domain.NotificationType;
import com.romif.securityalarm.domain.TrackingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.EnumSet;

@RestController
@RequestMapping("/api")
public class UtilResource {

    private final Logger log = LoggerFactory.getLogger(UtilResource.class);

    @GetMapping("/utils/trackingTypes")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<EnumSet<TrackingType>> getAllTrackingTypes() throws URISyntaxException {
        return new ResponseEntity<>(EnumSet.allOf(TrackingType.class), HttpStatus.OK);
    }

    @GetMapping("/utils/notificationTypes")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<EnumSet<NotificationType>> getAllNotificationTypes() throws URISyntaxException {
        return new ResponseEntity<>(EnumSet.allOf(NotificationType.class), HttpStatus.OK);
    }

}
