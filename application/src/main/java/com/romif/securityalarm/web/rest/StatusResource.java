package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.api.dto.StatusDto;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.domain.User;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.security.SecurityUtils;
import com.romif.securityalarm.service.StatusService;
import com.romif.securityalarm.service.dto.ImagesDTO;
import com.romif.securityalarm.service.dto.LocationDTO;
import com.romif.securityalarm.service.mapper.StatusMapper;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * REST controller for managing Status.
 */
@RestController
public class StatusResource {

    private final Logger log = LoggerFactory.getLogger(StatusResource.class);

    @Inject
    private StatusService statusService;

    @Inject
    private StatusMapper statusMapper;

    @Inject
    private DeviceRepository deviceRepository;

    /**
     * POST  /statuses : Create a new status.
     *
     * @param statusDto the status to create
     * @return the ResponseEntity with status 201 (Created) and with body the new status
     */
    @Secured("ROLE_DEVICE")
    @PostMapping(Constants.SEND_LOCATION_PATH)
    @Timed
    public ResponseEntity<StatusDto> saveStatus(@RequestBody StatusDto statusDto) {
        log.debug("REST request to save Status : {}", statusDto);

        Status status = statusService.save(statusMapper.statusDtoToStatus(statusDto));
        StatusDto result = statusMapper.statusToStatusDto(status);
        Queue<Status> statuses = statusService.getLast10StatusesCreatedBy(status.getCreatedBy());
        statusService.putInQueue(status, statuses);

        Optional<Device> device = deviceRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

        device
            .map(Device::getUser)
            .map(User::getMacAddresses)
            .ifPresent(macAddresses -> {
                result.setMacAddresses(macAddresses);
            });

        device.ifPresent(d -> {
            if (statusDto.getBalance() != null) {
                d.setBalance(statusDto.getBalance());
            }
            if (statusDto.getTraffic() != null) {
                d.setTraffic(statusDto.getTraffic());
            }
            deviceRepository.save(d);
        });
        result.getMacAddresses().size();

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
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

    @GetMapping("/api/statuses/locations")
    @Secured("ROLE_USER")
    @Timed
    public ResponseEntity<List<LocationDTO>> getLocations(@RequestParam(required = false) ZonedDateTime startDate,
                                                       @RequestParam(required = false) ZonedDateTime endDate,
                                                       @RequestParam Device device) {
        log.debug("REST request to get Locations");
        String login =  ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        if (device == null || !login.equals(device.getUser().getLogin())) {
            HttpHeaders headers = HeaderUtil.createFailureAlert("alarm", "deviceNotFound", "Device not found");
            return new ResponseEntity<>(Collections.emptyList(), headers, HttpStatus.BAD_REQUEST);
        }
        List<LocationDTO> locations = statusService.getLocationDTOs(startDate, endDate, device);

        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/api/statuses/images")
    @Secured("ROLE_USER")
    @Timed
    public ResponseEntity<List<ImagesDTO>> getImagesDTO(@RequestParam(required = false) ZonedDateTime startDate,
                                                   @RequestParam(required = false) ZonedDateTime endDate,
                                                   @RequestParam Device device) {
        log.debug("REST request to get Locations");
        String login =  ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        if (device == null || !login.equals(device.getUser().getLogin())) {
            HttpHeaders headers = HeaderUtil.createFailureAlert("alarm", "deviceNotFound", "Device not found");
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        }

        List<ImagesDTO> imagesDTOList = statusService.getImagesDTO(startDate, endDate, device);

        return new ResponseEntity<>(imagesDTOList, HttpStatus.OK);
    }

    @GetMapping("/api/statuses/video.{videoType}")
    @Secured("ROLE_USER")
    @Timed
    @ResponseBody
    public StreamingResponseBody getVideoMp4(@RequestParam ZonedDateTime startDate,
                                                @RequestParam ZonedDateTime endDate,
                                                @RequestParam Device device,
                                                @PathVariable String videoType,
                                                HttpServletResponse response) {
        log.debug("REST request to get video");

        String login =  ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if (device == null || !login.equals(device.getUser().getLogin())) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        response.setContentType("video/" + videoType);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        response.setHeader("Content-Disposition", "attachment; filename=video" + startDate.format(dateTimeFormatter) + "-" + endDate.format(dateTimeFormatter) + "." + videoType);


        if ("h264".equals(videoType)) {
            return outputStream -> {
                statusService.getVideoH264(startDate, endDate, device, outputStream);
            };
        } else if ("mp4".equals(videoType)) {
            return outputStream -> {
                statusService.getVideoMp4(startDate, endDate, device, outputStream);
            };
        }  else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }
    }

    /**
     * GET  /statuses/:id : get the "id" status.
     *
     * @param id the id of the status to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the status, or with status 404 (Not Found)
     */
    @Secured("ROLE_USER")
    @GetMapping("/api/statuses/{id}")
    @Timed
    public ResponseEntity<StatusDto> getStatus(@PathVariable Long id) {
        log.debug("REST request to get Status : {}", id);
        Optional<Status> status = statusService.findOne(id);
        return status
            .map(s -> {
                s.getImages().forEach(i -> i.getRawImage());

                return statusMapper.statusToStatusDto(s);
            })
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
