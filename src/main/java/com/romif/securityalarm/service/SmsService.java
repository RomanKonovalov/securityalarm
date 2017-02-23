package com.romif.securityalarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.config.JHipsterProperties;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceCredentials;
import com.romif.securityalarm.domain.sms.MessageRequest;
import com.romif.securityalarm.domain.sms.Request;
import com.romif.securityalarm.domain.sms.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

/**
 * Created by Roman_Konovalov on 2/23/2017.
 */
@Service
public class SmsService {

    private final Logger log = LoggerFactory.getLogger(SmsService.class);

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private Environment environment;

    @Autowired
    private JHipsterProperties jHipsterProperties;

    boolean sendConfig(Device device, DeviceCredentials deviceCredentials) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(deviceCredentials.getSecret());
        stringBuilder.append(0x0A);
        stringBuilder.append(device.getApn());
        stringBuilder.append(0x0A);
        stringBuilder.append(jHipsterProperties.getHttp().getHost());
        stringBuilder.append(0x0A);
        stringBuilder.append(Constants.SEND_LOCATION_PATH);
        stringBuilder.append(0x0A);
        stringBuilder.append(Constants.PAUSE_ALARM_PATH);
        stringBuilder.append(0x0A);
        stringBuilder.append(Constants.RESUME_ALARM_PATH);
        stringBuilder.append(0x0A);
        stringBuilder.append(deviceCredentials.getToken());
        stringBuilder.append(0x0A);

        try {
            Request request = new Request();
            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setNumber(device.getPhone());
            messageRequest.setText(stringBuilder.toString());
            request.setMessages(Arrays.asList(messageRequest));
            request.setTest(Arrays.asList(environment.getActiveProfiles()).contains(Constants.SPRING_PROFILE_DEVELOPMENT) ? true : false);

            String receiptUrl = UriComponentsBuilder.fromUriString("/api" + Constants.HANDLE_RECEIPTS_PATH)
                    .host(jHipsterProperties.getHttp().getHost()).scheme("http").toUriString();

            request.setReceiptUrl(receiptUrl);

            MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<String, String>();
            bodyMap.add("apikey", jHipsterProperties.getSecurity().getSms().getApikey());
            bodyMap.add("data", MAPPER.writeValueAsString(request));

            ResponseEntity<Response> model = REST_TEMPLATE.postForEntity(jHipsterProperties.getSecurity().getSms().getUrl(), bodyMap, Response.class);

            Response response = model.getBody();

            if ("success".equals(response.getStatus()) && CollectionUtils.isEmpty(response.getMessagesNotSent())) {
                return true;
            } else if (CollectionUtils.isNotEmpty(response.getErrors())) {
                log.error("Error while sending config: {}", response.getErrors().get(0).getMessage());
                return false;
            } else if (CollectionUtils.isNotEmpty(response.getMessagesNotSent())) {
                log.error("Error while sending config: {}", response.getMessagesNotSent().get(0).getMessage());
                return false;
            }
            return false;
        } catch (JsonProcessingException e) {
            log.error("Error while sending config", e);
            return false;
        }

    }
}
