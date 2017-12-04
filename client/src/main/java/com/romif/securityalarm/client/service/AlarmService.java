package com.romif.securityalarm.client.service;

import com.romif.securityalarm.api.config.Constants;
import com.romif.securityalarm.api.dto.StatusDto;
import com.romif.securityalarm.client.domain.huawei.Hosts;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AlarmService {

    private final Logger log = LoggerFactory.getLogger(AlarmService.class);

    @Value("${securityalarm.server.host}")
    private String host;

    @Value("${securityalarm.server.port}")
    private Integer port;

    private String url;

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Autowired
    private ModemService modemService;

    @Autowired
    private MotionService motionService;

    private ZonedDateTime lastPing = ZonedDateTime.now();

    private boolean alarmPaused = false;

    @PostConstruct
    public void init() {
        url = "http://" + host + ":" + port;
    }

    public boolean pauseAlarm() {
        try {
            restTemplate.getForObject(url + Constants.PAUSE_ALARM_PATH, Object.class);
            motionService.pauseDetection();
            return true;
        } catch (Exception e) {
            log.error("Error while pause alarm", e);
            return false;
        }
    }

    public boolean resumeAlarm() {
        try {
            restTemplate.getForObject(url + Constants.RESUME_ALARM_PATH, Object.class);
            motionService.startDetection();
            return true;
        } catch (Exception e) {
            log.error("Error while pause alarm", e);
            return false;
        }
    }

    /*public ZonedDateTime ping() {
        if (!alarmPaused) {
            alarmPaused = true;
            pauseAlarm();
        }
        synchronized (lastPing) {
            lastPing = ZonedDateTime.now();
            return lastPing;
        }
    }

    @Scheduled(fixedRate = 20000)
    public void checkPing() {
        if (alarmPaused && ZonedDateTime.now().toEpochSecond() - lastPing.toEpochSecond() > 60) {
            log.debug("Phone does not send pings for 60 seconds. Resuming alarm");
            resumeAlarm();
            alarmPaused = false;
        }
    }*/

    public void proceedStatus(StatusDto statusDto) {
        Set<String> macAddresses = modemService.getHosts().stream().map(Hosts.Host::getMacAddress).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(macAddresses) && CollectionUtils.isEmpty(statusDto.getMacAddresses())) {
            return;
        }
        Set<String> cleanMacAddresses = macAddresses.stream()
                .map(this::getCleanMacAddress)
                .collect(Collectors.toSet());
        if (CollectionUtils.containsAny(statusDto.getMacAddresses(), cleanMacAddresses)) {
            pauseAlarm();
        } else {
            resumeAlarm();
        }

    }

    public String getCleanMacAddress(String macAddress) {
        Matcher matcher = Constants.MAC_ADDRESS_PATTERN.matcher(macAddress);
        StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            for (int i = 1; i < 7; i++) {
                stringBuilder.append(matcher.group(i).toUpperCase());
            }
        }
        return stringBuilder.toString();
    }

}
