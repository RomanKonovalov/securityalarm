package com.romif.securityalarm.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SendReportService {

    private final Logger log = LoggerFactory.getLogger(SendReportService.class);

    @Scheduled(cron = "* * * * * *")
    public void sendReport() {

        log.debug("sendReport");

    }
}
