package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.repository.AlarmRepository;
import com.romif.securityalarm.security.AuthoritiesConstants;
import com.romif.securityalarm.service.StatusService;
import com.romif.securityalarm.web.rest.util.HeaderUtil;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AlarmResource {

    private final Logger log = LoggerFactory.getLogger(AlarmResource.class);

    @Inject
    private AlarmRepository alarmRepository;

    @Inject
    private StatusService statusService;

    @GetMapping("/alarms")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<Alarm>> getAllAlarms() throws URISyntaxException {
        String login =  ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        List<Alarm> alarms =  alarmRepository.findAll();

        return new ResponseEntity<>(alarms, HttpStatus.OK);
    }

    @Secured(AuthoritiesConstants.USER)
    @PostMapping("/alarms")
    @Timed
    @CacheEvict(cacheNames = "alarms", allEntries = true)
    public ResponseEntity<Alarm> startAlarm(@RequestBody Alarm alarm) throws URISyntaxException {
        log.debug("REST request to activate alarm");

        ResponseEntity<Alarm> response = checkAlarm(alarm);
        if (response != null) return response;

        Queue<Status> statuses = statusService.getLast10StatusesCreatedBy(alarm.getDevice().getLogin());

        if (statuses.stream().count() < 10) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("alarm", "notsufficientstatuses", "You don't have sufficient number of statuses. Please try again later")).body(null);
        }

        Alarm result = alarmRepository.save(alarm);
        return ResponseEntity.created(new URI("/api/alarm/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("alarm", result.getId().toString()))
            .body(result);
    }

    @Secured(AuthoritiesConstants.USER)
    @PutMapping("/alarms")
    @Timed
    @CacheEvict(cacheNames = "alarms", allEntries = true)
    public ResponseEntity<Alarm> updateAlarm(@RequestBody  Alarm alarm) throws URISyntaxException {
        log.debug("REST request to update Alarm : {}", alarm);
        if (alarm.getId() == null) {
            return startAlarm(alarm);
        }

        ResponseEntity<Alarm> response = checkAlarm(alarm);
        if (response != null) return response;

        Alarm result = alarmRepository.save(alarm);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("status", alarm.getId().toString()))
            .body(result);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/alarms/{id}")
    @Timed
    @CacheEvict(cacheNames = "alarms", allEntries = true)
    public ResponseEntity<Void> stopAlarm(@PathVariable Long id) {
        log.debug("REST request to deactivate alarm");
        alarmRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("alarm", id.toString())).build();
    }

    private ResponseEntity<Alarm> checkAlarm(Alarm alarm) {
        if (CollectionUtils.isEmpty(alarm.getTrackingTypes())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("alarm", "requiredfield", "Please choose Tracking Type")).body(null);
        }
        if (CollectionUtils.isEmpty(alarm.getNotificationTypes())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("alarm", "requiredfield", "Please choose Notification Type")).body(null);
        }
        return null;
    }

}
