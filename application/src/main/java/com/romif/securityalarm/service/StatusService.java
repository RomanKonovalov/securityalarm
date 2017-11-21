package com.romif.securityalarm.service;

import com.romif.securityalarm.api.dto.DeviceState;
import com.romif.securityalarm.domain.ConfigStatus;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.Image;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.repository.ImageRepository;
import com.romif.securityalarm.repository.StatusRepository;
import com.romif.securityalarm.service.dto.ImagesDTO;
import com.romif.securityalarm.service.dto.LocationDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service Implementation for managing Status.
 */
@Service
@Transactional
public class StatusService {

    public static final int MAX_VIDEO_FRAMES = 3701;
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

    public List<ImagesDTO> getImagesDTO (ZonedDateTime startDate, ZonedDateTime endDate, Device device) {

        List<Long> ids = imageRepository.findIds(startDate, endDate, device.getLogin(), new PageRequest(0, 260000));

        if (ids.size() < MAX_VIDEO_FRAMES * 4) {
            return ListUtils.partition(ids, MAX_VIDEO_FRAMES).stream().map(p -> getImagesDTO(p)).collect(Collectors.toList());
        } else {
            return Arrays.asList(getImagesDTO(ids));
        }

    }

    @NotNull
    private ImagesDTO getImagesDTO(List<Long> ids) {
        Image start = imageRepository.findOne(ids.get(0));
        Image end = imageRepository.findOne(ids.get(ids.size() - 1));
        ImagesDTO imagesDTO = new ImagesDTO();
        imagesDTO.setStartDate(start.getDateTime());
        imagesDTO.setEndDate(end.getDateTime());
        imagesDTO.setStart(start.getRawImage());
        imagesDTO.setEnd(end.getRawImage());
        return imagesDTO;
    }

    public List<LocationDTO> getLocationDTOs(ZonedDateTime startDate, ZonedDateTime endDate, Device device) {

        List<Long> ids = statusRepository.findIds(startDate, endDate, device.getLogin(), new PageRequest(0,260000));
        int nth = ids.size() > 100 ? ids.size() / 100 : 1;
        List<Long> filteredIds = IntStream.range(0, ids.size())
            .filter(n -> n % nth == 0)
            .mapToObj(ids::get)
            .collect(Collectors.toList());


        List<LocationDTO> locationDTOs = CollectionUtils.isEmpty(filteredIds) ? Collections.EMPTY_LIST : statusRepository.findByIds(filteredIds);
        return locationDTOs;
    }


    @Transactional(readOnly = true)
    public void getStatusVideo(ZonedDateTime startDate, ZonedDateTime endDate, Device device, OutputStream outputStream) throws AWTException, InterruptedException, IOException {

        List<Image> images = imageRepository.findByDateTimeAfterAndDateTimeBeforeAndStatusCreatedBy(startDate, endDate, device.getLogin(), new PageRequest(0, 1000, Sort.Direction.ASC, "dateTime"));

        videoService.getVideo(images, outputStream);
    }

    @Transactional(readOnly = true)
    public void getVideoH264(ZonedDateTime startDate, ZonedDateTime endDate, Device device, OutputStream outputStream) {

        List<Long> ids = imageRepository.findIds(startDate, endDate, device.getLogin(), new PageRequest(0, 260000));

        int nth = ids.size() > MAX_VIDEO_FRAMES ? ids.size() / MAX_VIDEO_FRAMES : 1;
        List<Long> filteredIds = IntStream.range(0, ids.size())
            .filter(n -> n % nth == 0)
            .mapToObj(ids::get)
            .collect(Collectors.toList());

        videoService.getVideoH264(filteredIds, outputStream);
    }

    @Transactional(readOnly = true)
    public void getVideoMp4(ZonedDateTime startDate, ZonedDateTime endDate, Device device, OutputStream outputStream) {

        //List<Image> images = imageRepository.findByDateTimeAfterAndDateTimeBeforeAndStatusCreatedBy(startDate, endDate, device.getLogin(), new PageRequest(0, 2000, Sort.Direction.ASC, "dateTime"));

        List<Long> ids = imageRepository.findIds(startDate, endDate, device.getLogin(), new PageRequest(0, 260000));

        int nth = ids.size() > 100 ? ids.size() / 100 : 1;
        List<Long> filteredIds = IntStream.range(0, ids.size())
            .filter(n -> n % nth == 0)
            .mapToObj(ids::get)
            .collect(Collectors.toList());


        videoService.getVideoMp4(ids, outputStream);
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
