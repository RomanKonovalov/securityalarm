package com.romif.securityalarm.client.service;

import com.romif.securityalarm.api.config.Constants;
import com.romif.securityalarm.api.dto.DeviceState;
import com.romif.securityalarm.api.dto.ImageDto;
import com.romif.securityalarm.api.dto.LocationDto;
import com.romif.securityalarm.api.dto.StatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class SendReportService implements ApplicationListener<VideoService.DiviceMotionEvent> {

    private final Logger log = LoggerFactory.getLogger(SendReportService.class);

    @Value("${securityalarm.server.host}")
    private String host;

    @Value("${securityalarm.server.port}")
    private Integer port;

    @Autowired
    private GPSService gpsService;

    @Autowired
    private VideoService videoService;

    private String statusUrl;

    private DeviceState deviceState;

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Autowired
    private SystemService systemService;

    @PostConstruct
    public void init() {
        statusUrl = "http://" + host + ":" + port + Constants.SEND_LOCATION_PATH;
    }

    @Scheduled(fixedRate = 20000)
    public void sendReport() {

        try {

            log.debug("sendReport()");

            LocationDto location = null;
            try {
                location = gpsService.getLocation();
            } catch (IOException e) {
                log.error("Can't get location", e);
            }

            log.debug("location");

            List<ImageDto> images = null;
            try {
                images = videoService.getImages();
            } catch (IOException e) {
                log.error("Can't get images", e);
            }

            log.debug("images");

            Integer deviceTemperature = systemService.getDeviceTemperature();

            StatusDto statusDto = new StatusDto();
            statusDto.setDeviceState(deviceState != null ? deviceState : DeviceState.OK);
            statusDto.setImages(images);
            statusDto.setLocation(location);
            statusDto.setDeviceTemperature(deviceTemperature);
            deviceState = null;

            try {
                log.debug("begin postForObject");
                StatusDto savedStatus = restTemplate.postForObject(statusUrl, statusDto, StatusDto.class);
                log.debug("savedStatus: " + savedStatus);
            } catch (RestClientException e) {
                log.error("Can't send status", e);
            }
        }catch (Exception e) {
            log.error("Can't send status", e);
        }

    }

    @Override
    public void onApplicationEvent(VideoService.DiviceMotionEvent diviceMotionEvent) {
        deviceState = DeviceState.MOTION;
    }
}
