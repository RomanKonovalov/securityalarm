package com.romif.securityalarm.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.romif.securityalarm.domain.*;
import com.romif.securityalarm.repository.AlarmRepository;
import com.romif.securityalarm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Service
public class AlarmService {

    private final Logger log = LoggerFactory.getLogger(AlarmService.class);

    @Inject
    private StatusService statusService;

    @Inject
    private AlarmRepository alarmRepository;

    @Inject
    private MailService mailService;

    @Inject
    private UserRepository userRepository;

    private Cache<String, Alarm> emailsMoving = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build();
    private Cache<String, Alarm> emailsInaccessible = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build();


    @Scheduled(cron = "* * * * * *")
    public void checkStatuses() {
        ZonedDateTime now = ZonedDateTime.now();


        alarmRepository.findAll().forEach(alarm -> {


            String login = alarm.getDevice().getLogin();
            if (alarm.getTrackingTypes().contains(TrackingType.INACCESSIBILITY)) {
                Optional<Status> status = statusService.getLastStatusCreatedBy(login);
                log.debug(status.toString());

                if (status.isPresent() && status.get().getCreatedDate().plusMinutes(1).isBefore(now)) {
                    log.debug("ALARM!! Device is inaccessible");

                    if (alarm.getNotificationTypes().contains(NotificationType.EMAIL)) {
                        if (emailsInaccessible.getIfPresent(login) == null) {
                            emailsInaccessible.put(login, alarm);
                            log.error("ALARM!! Device is inaccessible. Sending email");
                            Optional<User> user =  userRepository.findOneByLogin(alarm.getCreatedBy());

                            if (user.isPresent()) {
                                mailService.sendDeviceInaccessibleAlertEmail(user.get(), status.get());
                            }

                        }
                    }


                }

            }

            if (alarm.getTrackingTypes().contains(TrackingType.MOVEMENT)) {
                Queue<Status> statuses = statusService.getLast10StatusesCreatedBy(login);

                statuses.forEach(status -> log.debug(status.toString()));

                if (isDeviceMoving(statuses)) {

                    if (alarm.getNotificationTypes().contains(NotificationType.EMAIL)) {
                        if (emailsMoving.getIfPresent(login) == null) {
                            emailsMoving.put(login, alarm);
                            log.error("ALARM!! Device is moving. Sending email");
                            Optional<User> user =  userRepository.findOneByLogin(alarm.getCreatedBy());

                            if (user.isPresent()) {
                                mailService.sendDeviceMovingAlertEmail(user.get(), statuses.element());
                            }

                        }
                    }

                    log.debug("ALARM!! Device is moving");
                }


            }


        });



    }

    private boolean isDeviceMoving(Queue<Status> statuses) {
        return true;
    }
}
