package com.romif.securityalarm.config;

import com.romif.securityalarm.repository.DeviceCredentialsRepository;
import com.romif.securityalarm.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            .forEach(deviceCredentials -> {
                securityService.authenticate(deviceCredentials);
            });

        log.info("Authenticating users done");
    }


}
