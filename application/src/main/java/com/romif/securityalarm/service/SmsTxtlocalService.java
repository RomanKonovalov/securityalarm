package com.romif.securityalarm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.config.ApplicationProperties;
import com.romif.securityalarm.domain.ConfigStatus;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceCredentials;
import com.romif.securityalarm.domain.GenericUser;
import com.romif.securityalarm.domain.sms.*;
import com.romif.securityalarm.repository.UserRepository;
import com.romif.securityalarm.security.AuthoritiesConstants;
import com.romif.securityalarm.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Roman_Konovalov on 2/23/2017.
 */
@Service
public class SmsTxtlocalService {

    private final Logger log = LoggerFactory.getLogger(SmsTxtlocalService.class);

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ConcurrentMap<String, CompletableFuture<ConfigStatus>> reciepts = new ConcurrentHashMap<>();

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private UserRepository userRepository;

    public CompletableFuture<ConfigStatus> sendConfig(Device device, DeviceCredentials deviceCredentials) {

        String stringBuilder = deviceCredentials.getSecret() +
            ";" +
            device.getApn() +
            ";" +
            applicationProperties.getHttp().getHost() +
            ";" +
            Constants.SEND_LOCATION_PATH +
            ";" +
            Constants.PAUSE_ALARM_PATH +
            ";" +
            Constants.RESUME_ALARM_PATH +
            ";" +
            deviceCredentials.getToken() +
            ";" +
            StringUtils.remove(device.getUser().getPhone(), "+") +
            ";";

        Request request = new Request();
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setNumber(device.getPhone());
        messageRequest.setText(stringBuilder);
        request.setMessages(Collections.singletonList(messageRequest));
        request.setTest(Arrays.asList(environment.getActiveProfiles()).contains(Constants.SPRING_PROFILE_DEVELOPMENT));

        String receiptUrl = UriComponentsBuilder.fromUriString("/api" + Constants.HANDLE_RECEIPTS_PATH)
            .host(applicationProperties.getHttp().getHost()).scheme("http").toUriString();

        request.setReceiptUrl(receiptUrl);

        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("apikey", applicationProperties.getSecurity().getSms().getApikey());
        bodyMap.add("data", new Gson().toJson(request));

        ResponseEntity<Response> model = REST_TEMPLATE.postForEntity(applicationProperties.getSecurity().getSms().getUrl(), bodyMap, Response.class);

        Response response = model.getBody();

        if ("success".equals(response.getStatus()) && CollectionUtils.isEmpty(response.getMessagesNotSent())) {

            CompletableFuture<ConfigStatus> result = new CompletableFuture<>();
            reciepts.put(device.getPhone(), result);

            return result;
        } else if (CollectionUtils.isNotEmpty(response.getErrors())) {
            log.error("Error while sending config: {}", response.getErrors().get(0).getMessage());
            return CompletableFuture.completedFuture(ConfigStatus.NOT_CONFIGURED);
        } else if (CollectionUtils.isNotEmpty(response.getMessagesNotSent())) {
            log.error("Error while sending config: {}", response.getMessagesNotSent().get(0).getMessage());
            return CompletableFuture.completedFuture(ConfigStatus.NOT_CONFIGURED);
        }
        return CompletableFuture.completedFuture(ConfigStatus.NOT_CONFIGURED);

    }

    public void handleReceipt(Receipt receipt) {
        String phone = receipt.getNumber();
        if (!phone.startsWith("+")) {
            phone = "+" + phone;
        }

        CompletableFuture<ConfigStatus> future = reciepts.get(phone);
        if (future != null) {
            switch (receipt.getStatus()) {
                case DELIVERED:
                    future.complete(ConfigStatus.CONFIG_SENT);
                    break;
                case PENDING:
                    future.complete(ConfigStatus.PENDING);
                    break;
                default:
                    future.complete(ConfigStatus.NOT_CONFIGURED);
            }

            reciepts.remove(phone);
        }

    }

    @Secured(AuthoritiesConstants.ADMIN)
    public BigDecimal getBalance() {
        String login = SecurityUtils.getCurrentUserLogin();

        String phone = userRepository.findOneByLogin(login).map(GenericUser::getPhone).orElse("1234567890");

        Request request = new Request();
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setNumber(phone);
        messageRequest.setText("text");
        request.setMessages(Collections.singletonList(messageRequest));
        request.setTest(true);

        String receiptUrl = UriComponentsBuilder.fromUriString("/api" + Constants.HANDLE_RECEIPTS_PATH)
            .host(applicationProperties.getHttp().getHost()).scheme("http").toUriString();

        request.setReceiptUrl(receiptUrl);

        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("apikey", applicationProperties.getSecurity().getSms().getApikey());
        bodyMap.add("data", new Gson().toJson(request));

        ResponseEntity<Response> model = REST_TEMPLATE.postForEntity(applicationProperties.getSecurity().getSms().getUrl(), bodyMap, Response.class);

        Response response = model.getBody();

        if ("success".equals(response.getStatus()) && CollectionUtils.isEmpty(response.getMessagesNotSent())) {
            return response.getBalancePostSend().divide(response.getMessages().get(0).getCost(), 0, RoundingMode.DOWN);
        } else if (CollectionUtils.isNotEmpty(response.getErrors())) {
            log.error("Error while sending config: {}", response.getErrors().get(0).getMessage());
            return null;
        } else if (CollectionUtils.isNotEmpty(response.getMessagesNotSent())) {
            log.error("Error while sending config: {}", response.getMessagesNotSent().get(0).getMessage());
            return null;
        }
        return null;
    }
}
