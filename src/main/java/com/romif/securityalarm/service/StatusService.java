package com.romif.securityalarm.service;

import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.domain.User;
import com.romif.securityalarm.repository.StatusRepository;
import com.romif.securityalarm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Status.
 */
@Service
@Transactional
public class StatusService {

    private final Logger log = LoggerFactory.getLogger(StatusService.class);

    @Inject
    private StatusRepository statusRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * Save a status.
     *
     * @param status the entity to save
     * @return the persisted entity
     */

    @CachePut(value = "status", key = "#createdBy")
    public Status putCashe(String createdBy, Status status) {
        log.debug("Request to save putCashe");
        return status;
    }

    public Status save(Status status) {
        log.debug("Request to save Status : {}", status);
        Status result = statusRepository.save(status);
        return putCashe(result.getCreatedBy(), result);
    }

    /**
     *  Get all the statuses.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Status> findAll(Pageable pageable) {
        log.debug("Request to get all Statuses");
        Page<Status> result = statusRepository.findAll(pageable);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<Status> findAll(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate) {
        log.debug("Request to get all Statuses");
        Page<Status> result;
        if (startDate != null && endDate != null) {
            result = statusRepository.findByCreatedDateAfterAndCreatedDateBefore(startDate, endDate, pageable);
        } else {
            result = statusRepository.findAll(pageable);
        }

        return result;
    }

    /**
     *  Get one status by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Status findOne(Long id) {
        log.debug("Request to get Status : {}", id);
        Status status = statusRepository.findOne(id);
        return status;
    }

    /**
     *  Delete the  status by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Status : {}", id);
        statusRepository.delete(id);
    }

    @Cacheable(value = "status", key = "#createdBy")
    public Optional<Status> findOneByLogin(String createdBy) {
        log.debug("Request to get Status createdBy: {}", createdBy);
        Optional<Status> status = statusRepository.findFirstByCreatedBy(createdBy);
        return status;
    }

    public Set<Status> getRecentStatuses() {
        return userRepository.findAllUserLogins().stream().map(login -> findOneByLogin(login))
            .filter(status -> status.isPresent())
            .map(status -> status.get())
            .collect(Collectors.toSet());
    }

    @Scheduled(cron = "* * * * * *")
    public void checkStatuses() {
        Set<Status> statuses = getRecentStatuses();

        statuses.forEach(status -> log.debug(status.toString()));

    }


}
