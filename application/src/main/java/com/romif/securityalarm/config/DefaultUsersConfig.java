package com.romif.securityalarm.config;

import com.romif.securityalarm.repository.DeviceCredentialsRepository;
import com.romif.securityalarm.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by Roman_Konovalov on 1/17/2017.
 */
@Component
public class DefaultUsersConfig {

    private static final Logger log = LoggerFactory.getLogger(DefaultUsersConfig.class);

    @Inject
    private DeviceCredentialsRepository deviceCredentialsRepository;

    @Inject
    private SecurityService securityService;

    @EventListener(ContextRefreshedEvent.class)
    public void authenticateDefaultUsers() {
        log.info("Authenticating users ...");

        deviceCredentialsRepository.findAll()
            .forEach(deviceCredentials -> securityService.authenticate(deviceCredentials));

        log.info("Authenticating users done");
    }


}
