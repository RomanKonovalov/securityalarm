package com.romif.securityalarm.service;

import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.repository.AlarmRepository;
import com.romif.securityalarm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlarmService {

    private final Logger log = LoggerFactory.getLogger(AlarmService.class);

    @Inject
    private StatusService statusService;

    @Inject
    private AlarmRepository alarmRepository;

    public Set<Status> getRecentStatuses() {
        return alarmRepository.findAll().stream()
            .map(alarm -> statusService.getLastStatusCreatedBy(alarm.getDeviceName()))
            .filter(status -> status.isPresent())
            .map(status -> status.get())
            .collect(Collectors.toSet());
    }

    @Scheduled(cron = "* * * * * *")
    public void checkStatuses() {
        ZonedDateTime now = ZonedDateTime.now();
        Set<Status> statuses = getRecentStatuses();

        statuses.forEach(status -> {
            log.debug(status.toString());
            if (status.getCreatedDate().plusMinutes(1).isBefore(now)) {
                log.error("ALARM!!");
            }


        });

    }
}
