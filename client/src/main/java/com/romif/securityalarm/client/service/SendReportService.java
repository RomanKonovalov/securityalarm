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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Collectors;

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

    @Autowired
    private WebCamService webCamService;

    private String statusUrl;

    @PostConstruct
    public void init() {
        statusUrl = "http://" + host + ":" + port + Constants.SEND_LOCATION_PATH;
    }

    @Scheduled(cron = "* * * * * *")
    public void sendReport() throws URISyntaxException, IOException {

        //gpsService.getLocation();

        byte[] image = null;
        try {
            image = webCamService.getImage();
        } catch (IOException e) {
            log.error("Error while taking image", e);
        }

        StatusDto statusDto = new StatusDto();
        statusDto.setDeviceState(DeviceState.OK);
        statusDto.setImage(image);

        try {
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
                log.error("Cannot send status: " +  new BufferedReader(new InputStreamReader(con.getErrorStream()))
                        .lines().collect(Collectors.joining("\n")));
            } else {
                StatusDto savedStatus = mapper.readValue(con.getInputStream(), StatusDto.class);
                log.debug("savedStatus: " + savedStatus);
            }

        } catch (ConnectException e) {
            log.error("Can't send status, host is not reachable: " + statusUrl);
        }

    }


}
