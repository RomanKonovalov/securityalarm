package com.romif.securityalarm.client.service;

import com.romif.securityalarm.api.dto.LocationDto;
import de.taimos.gpsd4java.api.IObjectListener;
import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import de.taimos.gpsd4java.types.*;
import de.taimos.gpsd4java.types.subframes.SUBFRAMEObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

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
            /*gpSdEndpoint.addListener(new IObjectListener() {
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
            });*/
            gpSdEndpoint.start();
            gpSdEndpoint.watch(true, true);
        } catch (IOException e) {
            log.error("Unable to init gpSdEndpoint", e);
        }
    }

    public LocationDto getLocation() throws IOException {
        if (gpSdEndpoint == null) {
            return null;
        }
        PollObject pollObject = gpSdEndpoint.poll();

        if (pollObject != null && !CollectionUtils.isEmpty(pollObject.getFixes())) {
            TPVObject tpvObject = pollObject.getFixes().get(0);
            return new LocationDto(tpvObject.getTimestamp(), tpvObject.getTimestampError(), tpvObject.getLatitude(), tpvObject.getLongitude(), tpvObject.getAltitude(), tpvObject.getLatitudeError(), tpvObject.getLongitudeError(), tpvObject.getAltitudeError(), tpvObject.getCourse(), tpvObject.getSpeed(), tpvObject.getClimbRate(), tpvObject.getCourseError(), tpvObject.getSpeedError(), tpvObject.getClimbRateError());
        }
        return null;
    }


}
