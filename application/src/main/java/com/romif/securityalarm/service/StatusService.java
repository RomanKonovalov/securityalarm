package com.romif.securityalarm.service;

import com.romif.securityalarm.api.dto.DeviceState;
import com.romif.securityalarm.domain.ConfigStatus;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.Image;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.repository.ImageRepository;
import com.romif.securityalarm.repository.StatusRepository;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.List;
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
    private VideoService videoService;

    @Inject
    private ImageService imageService;

    @Inject
    private ImageRepository imageRepository;

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
            status.setThumbnail(imageService.getThumbnail(status.getImages()));
        } catch (IOException e) {
            log.error("Error while making thumbnail for status {}", status);
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

    @Transactional(readOnly = true)
    public void getStatusVideo(ZonedDateTime startDate, ZonedDateTime endDate, Device device, OutputStream outputStream) throws AWTException, InterruptedException, IOException {

        List<Image> images = imageRepository.findByDateTimeAfterAndDateTimeBeforeAndStatusCreatedByOrderByDateTimeAsc(startDate, endDate, device.getLogin());

        videoService.getVideo(images, outputStream);
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
