package com.romif.securityalarm.client.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.romif.securityalarm.client.config.Constants;
import com.romif.securityalarm.client.domain.huawei.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ModemService {

    private final Logger log = LoggerFactory.getLogger(ModemService.class);

    private RestTemplate restTemplate = new RestTemplate();

    private XmlMapper xmlMapper = new XmlMapper();

    private Pattern BALANCE_PATTERN = Pattern.compile("^.*\\s-?(\\d+)р\\.(\\d+)к\\..*\\R?.*$");

    private Pattern TRAFFIC_PATTERN = Pattern.compile("^(\\d+)\\.(\\d*).*$");

    @Value("${modem.username}")
    private String username;

    @Value("${mode.password}")
    private String password;

    @PostConstruct
    public void init() {
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2XmlHttpMessageConverter) {
                MappingJackson2XmlHttpMessageConverter jsonConverter = (MappingJackson2XmlHttpMessageConverter) converter;
                ArrayList<MediaType> mediaTypes = new ArrayList<>(jsonConverter.getSupportedMediaTypes());
                mediaTypes.add(new MediaType("text", "html", MappingJackson2XmlHttpMessageConverter.DEFAULT_CHARSET));
                jsonConverter.setSupportedMediaTypes(mediaTypes);
            }
        }

    }

    public Status getStatus() {
        Status status = restTemplate.getForObject(Constants.Modem.HOST + Constants.Modem.MONITORING_STATUS_PATH, Status.class);
        return status;
    }

    public List<Hosts.Host> getHosts() {
        try {
            String xml = restTemplate.getForObject(Constants.Modem.HOST + Constants.Modem.HOST_LIST_PATH, String.class);
            xml = xml.replaceAll("\r\n", "");
            Hosts hosts = xmlMapper.readValue(xml, Hosts.class);
            return hosts.getHosts() != null ? hosts.getHosts() : Collections.emptyList();
        } catch (ResourceAccessException | IOException e) {
            log.error("Can't get hosts", e);
            return Collections.emptyList();
        }
    }

    public CompletableFuture<String> sendUSSD(String ussd) {
        login();
        UssdResponse response = restTemplate.postForObject(Constants.Modem.HOST + Constants.Modem.SEND_USSD_PATH, new UssdRequest(ussd, "CodeType"), UssdResponse.class);

        if (response.getCode() != null) {
            return CompletableFuture.completedFuture("error: code " + response.getCode());
        }

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        AtomicInteger atomicInteger = new AtomicInteger();

        Runnable getUssdTask = () -> {
            try {
                UssdStatusResponse ussdStatusResponse = restTemplate.getForObject(Constants.Modem.HOST + Constants.Modem.USSD_STATUS_PATH, UssdStatusResponse.class);

                UssdResponse ussdResponse = restTemplate.getForObject(Constants.Modem.HOST + Constants.Modem.GET_USSD_PATH, UssdResponse.class);
                if (ussdResponse.getContent() != null) {
                    completableFuture.complete(ussdResponse.getContent());
                    scheduledExecutorService.shutdown();
                }
                if (ussdStatusResponse.getResult() == 1) {

                }

            } catch (ResourceAccessException e) {
                log.error("Can't get ussd", e);
                completableFuture.complete("error: " + e.getLocalizedMessage());
                scheduledExecutorService.shutdown();
            }

            if (atomicInteger.getAndIncrement() > 50) {
                log.error("Can't get ussd, timeout");
                completableFuture.complete("error: timeout");
                scheduledExecutorService.shutdown();
            }

        };

        scheduledExecutorService.scheduleAtFixedRate(getUssdTask, 500, 200, TimeUnit.MILLISECONDS);

        return completableFuture;
    }

    public void login() {
        LoginRequest loginRequest = new LoginRequest(username, Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)));
        restTemplate.postForObject(Constants.Modem.HOST + Constants.Modem.LOGIN_PATH, loginRequest, Object.class);
    }

    public void logout() {
        restTemplate.postForObject(Constants.Modem.HOST + Constants.Modem.LOGOUT_PATH, new LogoutRequest(1), Object.class);
    }

    public CompletableFuture<BigDecimal> getBalance() {
        return sendUSSD("*100#").thenApply(result -> {
            Matcher matcher = BALANCE_PATTERN.matcher(result);
            if (matcher.find()) {
                return new BigDecimal(matcher.group(1) + "." + matcher.group(2));
            } else {
                return null;
            }
        });
    }

    public CompletableFuture<BigDecimal> getTraffic() {
        return sendUSSD("*100*1#").thenApply(result -> {
            Matcher matcher = TRAFFIC_PATTERN.matcher(result);
            if (matcher.find()) {
                return new java.math.BigDecimal(matcher.group(1) + "." + matcher.group(2));
            } else {
                return null;
            }
        });
    }
}
