package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.repository.AlarmRepository;
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
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AlarmResource {

    private final Logger log = LoggerFactory.getLogger(AlarmResource.class);

    @Inject
    private AlarmRepository alarmRepository;

    @Secured("ROLE_USER")
    @PostMapping("/alarm")
    @Timed
    public ResponseEntity<Alarm> startAlarm() throws URISyntaxException {
        log.debug("REST request to start alarm");

        Alarm result = alarmRepository.save(new Alarm());
        return ResponseEntity.created(new URI("/api/alarm/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("alarm", result.getId().toString()))
            .body(result);
    }


    @Secured("ROLE_USER")
    @DeleteMapping("/alarm")
    @Timed
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        alarmRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("alarm", id.toString())).build();
    }

}
