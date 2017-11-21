package com.romif.securityalarm.client.web.rest;

import com.romif.securityalarm.api.config.AuthoritiesConstants;
import com.romif.securityalarm.api.config.Constants;
import com.romif.securityalarm.client.service.AlarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@EnableOAuth2Sso
public class AlarmResource {

    private final Logger log = LoggerFactory.getLogger(AlarmResource.class);

    @Autowired
    private AlarmService alarmService;

    @Secured(AuthoritiesConstants.USER)
    @RequestMapping(Constants.PAUSE_ALARM_PATH)
    public ResponseEntity<Void> pauseAlarm() {
        log.debug("REST request to pause alarm");
        if (alarmService.pauseAlarm()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured(AuthoritiesConstants.USER)
    @RequestMapping(Constants.RESUME_ALARM_PATH)
    public ResponseEntity<Void> resumeAlarm() {
        log.debug("REST request to resume alarm");
        if (alarmService.resumeAlarm()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured(AuthoritiesConstants.USER)
    @RequestMapping(Constants.PING_DEVICE_PATH)
    public ResponseEntity<ZonedDateTime> devicePing() {
        log.debug("ping");
        return ResponseEntity.ok(alarmService.ping());
    }
}
