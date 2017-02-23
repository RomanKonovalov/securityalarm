package com.romif.securityalarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.config.JHipsterProperties;
import com.romif.securityalarm.domain.Device;
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

    boolean sendConfig(Device device) {

        try {
            Request request = new Request();
            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setNumber("+375445486323");
            messageRequest.setText("Test text");
            request.setMessages(Arrays.asList(messageRequest));
            request.setTest(Arrays.asList(environment.getActiveProfiles()).contains(Constants.SPRING_PROFILE_DEVELOPMENT) ? true : false);

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
