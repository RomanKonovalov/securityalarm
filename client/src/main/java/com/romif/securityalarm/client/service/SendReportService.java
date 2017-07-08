package com.romif.securityalarm.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romif.securityalarm.api.config.Constants;
import com.romif.securityalarm.api.dto.DeviceState;
import com.romif.securityalarm.api.dto.StatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SendReportService {

    private final Logger log = LoggerFactory.getLogger(SendReportService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${securityalarm.server.host}")
    private String host;

    @Value("${securityalarm.server.port}")
    private Integer port;

    @Value("${securityalarm.server.token}")
    private String token;

    @Autowired
    private GPSService gpsService;

    private String statusUrl;

    @PostConstruct
    public void init() {
        statusUrl = "http://" + host + ":" + port + Constants.SEND_LOCATION_PATH;
    }

    @Scheduled(cron = "* * * * * *")
    public void sendReport() throws IOException {

        gpsService.getLocation();

        StatusDto statusDto = new StatusDto();
        statusDto.setDeviceState(DeviceState.OK);

        URL obj = new URL(statusUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
        outputStream.writeBytes(mapper.writeValueAsString(statusDto));
        outputStream.flush();
        outputStream.close();

        int responseCode = con.getResponseCode();
        if (HttpURLConnection.HTTP_CREATED != responseCode) {
            log.error("Cannot send status. Code: " + responseCode);
        }

        StatusDto savedStatus = mapper.readValue(con.getInputStream(), StatusDto.class);

        log.debug("savedStatus: " + savedStatus);

    }
}
