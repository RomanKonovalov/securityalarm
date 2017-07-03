package com.romif.securityalarm.client.service;

import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class GPSService {

    private final Logger log = LoggerFactory.getLogger(GPSService.class);

    @Value("${securityalarm.gps.host}")
    private String host;

    @Value("${securityalarm.gps.port}")
    private Integer port;

    private ResultParser resultParser = new ResultParser();

    private GPSdEndpoint gpSdEndpoint;

    @PostConstruct
    public void init() {
        try {
            gpSdEndpoint = new GPSdEndpoint(host, port, resultParser);
        } catch (IOException e) {
            log.error("Unable to init gpSdEndpoint", e);
        }
    }


}
