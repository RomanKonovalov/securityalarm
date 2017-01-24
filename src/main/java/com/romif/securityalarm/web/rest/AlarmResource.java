package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.repository.AlarmRepository;
import com.romif.securityalarm.security.AuthoritiesConstants;
import com.romif.securityalarm.service.StatusService;
import com.romif.securityalarm.web.rest.util.HeaderUtil;
import com.romif.securityalarm.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class AlarmResource {

    private final Logger log = LoggerFactory.getLogger(AlarmResource.class);

    @Inject
    private AlarmRepository alarmRepository;

    @GetMapping("/alarms")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Set<Alarm>> getAllAlarms() throws URISyntaxException {
        String login =  ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        Set<Alarm> alarms =  alarmRepository.findAllByCreatedBy(login);

        return new ResponseEntity<>(alarms, HttpStatus.OK);
    }

    @Secured(AuthoritiesConstants.USER)
    @PostMapping("/alarms")
    @Timed
    public ResponseEntity<Alarm> startAlarm(@RequestBody Alarm alarm) throws URISyntaxException {
        log.debug("REST request to activate alarm");

        Alarm result = alarmRepository.save(alarm);
        return ResponseEntity.created(new URI("/api/alarm/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("alarm", result.getId().toString()))
            .body(result);
    }

    @Secured(AuthoritiesConstants.USER)
    @PutMapping("/alarms")
    @Timed
    public ResponseEntity<Alarm> updateStatus(@RequestBody  Alarm alarm) throws URISyntaxException {
        log.debug("REST request to update Alarm : {}", alarm);
        if (alarm.getId() == null) {
            return startAlarm(alarm);
        }
        Alarm result = alarmRepository.save(alarm);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("status", alarm.getId().toString()))
            .body(result);
    }


    @Secured("ROLE_USER")
    @DeleteMapping("/alarms/{id}")
    @Timed
    public ResponseEntity<Void> stopAlarm(@PathVariable Long id) {
        log.debug("REST request to deactivate alarm");
        alarmRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("alarm", id.toString())).build();
    }

}
