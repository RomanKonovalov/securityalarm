package com.romif.securityalarm.client.service;

import de.taimos.gpsd4java.api.IObjectListener;
import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import de.taimos.gpsd4java.types.*;
import de.taimos.gpsd4java.types.subframes.SUBFRAMEObject;
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

    private final ResultParser resultParser = new ResultParser();

    private GPSdEndpoint gpSdEndpoint;

    @PostConstruct
    public void init() {
        try {
            gpSdEndpoint = new GPSdEndpoint(host, port, resultParser);
            gpSdEndpoint.addListener(new IObjectListener() {
                public void handleTPV(TPVObject tpv) {
                    System.out.println( "Hello World!" );
                }

                public void handleSKY(SKYObject sky) {
                    System.out.println( "Hello World!" );
                }

                public void handleATT(ATTObject att) {
                    System.out.println( "Hello World!" );
                }

                public void handleSUBFRAME(SUBFRAMEObject subframe) {
                    System.out.println( "Hello World!" );
                }

                public void handleDevices(DevicesObject devices) {
                    System.out.println( "Hello World!" );
                }

                public void handleDevice(DeviceObject device) {
                    System.out.println( "Hello World!" );
                }
            });
            gpSdEndpoint.start();
        } catch (IOException e) {
            log.error("Unable to init gpSdEndpoint", e);
        }
    }

    public void getLocation() throws IOException {
        PollObject pollObject = gpSdEndpoint.poll();
        pollObject.getFixes();
    }


}
