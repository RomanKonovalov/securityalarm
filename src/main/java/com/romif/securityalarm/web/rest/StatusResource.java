package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.service.StatusService;
import com.romif.securityalarm.web.rest.util.HeaderUtil;
import com.romif.securityalarm.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;

/**
 * REST controller for managing Status.
 */
@RestController
public class StatusResource {

    private final Logger log = LoggerFactory.getLogger(StatusResource.class);

    @Inject
    private StatusService statusService;

    /**
     * POST  /statuses : Create a new status.
     *
     * @param status the status to create
     * @return the ResponseEntity with status 201 (Created) and with body the new status, or with status 400 (Bad Request) if the status has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Secured("ROLE_DEVICE")
    @PostMapping(Constants.SEND_LOCATION_PATH)
    @Timed
    public ResponseEntity<?> createStatus(@RequestBody Status status) throws URISyntaxException {
        log.debug("REST request to save Status : {}", status);
        if (status.getId() != null) {

            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("status", "idexists", "A new status cannot already have an ID")).body(null);
        }
        Status result = statusService.save(status);
        Queue<Status> statuses =  statusService.getLast10StatusesCreatedBy(result.getCreatedBy());
        statusService.putInQueue(result, statuses);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * PUT  /statuses : Updates an existing status.
     *
     * @param status the status to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated status,
     * or with status 400 (Bad Request) if the status is not valid,
     * or with status 500 (Internal Server Error) if the status couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Secured("ROLE_ADMIN")
    @PutMapping("/api/statuses")
    @Timed
    public ResponseEntity<?> updateStatus(@RequestBody Status status) throws URISyntaxException {
        log.debug("REST request to update Status : {}", status);
        if (status.getId() == null) {
            return createStatus(status);
        }
        Status result = statusService.save(status);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("status", status.getId().toString()))
            .body(result);
    }

    /**
     * GET  /statuses : get all the statuses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of statuses in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/api/statuses")
    @Secured("ROLE_USER")
    @Timed
    public ResponseEntity<List<Status>> getAllStatuses(@ApiParam Pageable pageable,
                                                       @RequestParam(required = false) ZonedDateTime startDate,
                                                       @RequestParam(required = false) ZonedDateTime endDate,
                                                       @RequestParam Device device)
        throws URISyntaxException {
        log.debug("REST request to get a page of Statuses");
        String login =  ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        if (device == null || !login.equals(device.getUser().getLogin())) {
            HttpHeaders headers = HeaderUtil.createFailureAlert("alarm", "deviceNotFound", "Device not found");
            return new ResponseEntity<>(Collections.emptyList(), headers, HttpStatus.BAD_REQUEST);
        }
        Page<Status> page = statusService.findAll(pageable, startDate, endDate, device);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /statuses/:id : get the "id" status.
     *
     * @param id the id of the status to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the status, or with status 404 (Not Found)
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/api/statuses/{id}")
    @Timed
    public ResponseEntity<Status> getStatus(@PathVariable Long id) {
        log.debug("REST request to get Status : {}", id);
        Status status = statusService.findOne(id);
        return Optional.ofNullable(status)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /statuses/:id : delete the "id" status.
     *
     * @param id the id of the status to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/api/statuses/{id}")
    @Timed
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        log.debug("REST request to delete Status : {}", id);
        statusService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("status", id.toString())).build();
    }

}
