package com.romif.securityalarm.service;

import com.romif.securityalarm.api.dto.DeviceState;
import com.romif.securityalarm.config.ApplicationProperties;
import com.romif.securityalarm.domain.*;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.repository.StatusRepository;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

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
    private DeviceRepository deviceRepository;

    @Inject
    private ApplicationProperties applicationProperties;

    @Inject
    private ImageService imageService;

    /**
     * Save a status.
     *
     * @param status the entity to save
     * @return the persisted entity
     */
    @CachePut(value = "status", key = "#result.createdBy")
    public Status save(Status status) {
        log.debug("Request to save Status : {}", status);
        try {
            status.setThumbnail(imageService.getThumbnail(status.getImage()));
        } catch (IOException e) {
            log.error("Error while making thumbnail for status {}", status);
            e.printStackTrace();
        }
        Status result = statusRepository.save(status);
        if (DeviceState.CONFIGURED.equals(status.getDeviceState())) {
            deviceRepository.findOneByLogin(result.getCreatedBy())
                .ifPresent(device -> {
                    device.setConfigStatus(ConfigStatus.CONFIGURED);
                    deviceRepository.save(device);
                });
        }
        return result;
    }

    @CachePut(value = "statusQueue", key = "#status.createdBy")
    public Queue<Status> putInQueue(Status status, Queue<Status> statusQueue) {
        statusQueue.add(status);
        return statusQueue;
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
    public Page<Status> findAll(Pageable pageable, ZonedDateTime startDate, ZonedDateTime endDate, Device device) {
        log.debug("Request to get all Statuses");
        Page<Status> result;
        if (startDate != null && endDate != null) {
            result = statusRepository.findByCreatedDateAfterAndCreatedDateBeforeAndCreatedBy(startDate, endDate, device.getLogin(), pageable);
        } else {
            result = statusRepository.findByCreatedBy(device.getLogin(), pageable);
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
    public Optional<Status> findOne(Long id) {
        log.debug("Request to get Status : {}", id);
        Optional<Status> status = statusRepository.findOneById(id);
        return status.map(s -> {
            File folder = new File(applicationProperties.getImage().getStoragePath() + File.separator + s.getCreatedBy());
            File imageFile = new File(folder, File.separator + s.getId() + ".jpg");
            try {
                s.setImage(FileUtils.readFileToByteArray(imageFile));
            } catch (IOException e) {
                log.error("Error while reading image", e);
            }
            return s;

        });

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
    public Optional<Status> getLastStatusCreatedBy(String createdBy) {
        return statusRepository.findFirstByCreatedByOrderByCreatedDateDesc(createdBy);
    }

    @Cacheable(value = "statusQueue", key = "#createdBy")
    public Queue<Status> getLast10StatusesCreatedBy(String createdBy) {
        Set<Status> statuses = statusRepository.findTop10ByCreatedByOrderByCreatedDateDesc(createdBy);
        Queue<Status> statusQueue = new CircularFifoQueue<>(10);
        statusQueue.addAll(statuses);
        return statusQueue;
    }

}
