package com.romif.securityalarm.service;

import com.romif.securityalarm.config.JHipsterProperties;
import com.romif.securityalarm.repository.DeviceCredentialsRepository;
import com.romif.securityalarm.service.dto.DeviceDTO;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    @Inject
    private TokenStore tokenStore;

    @Inject
    private JHipsterProperties jHipsterProperties;

    @Inject
    private DeviceCredentialsRepository deviceCredentialsRepository;

    public List<DeviceDTO> getAllDevices() {

        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(jHipsterProperties.getSecurity().getAuthentication().getOauth().getClientid());

        List<DeviceDTO> deviceDTOS = deviceCredentialsRepository.findAll().stream()
            .map(deviceCredentials -> {
                DeviceDTO deviceDTO = new DeviceDTO(deviceCredentials.getDevice());
                deviceDTO.setAuthorized(tokens.stream().anyMatch(oAuth2AccessToken -> oAuth2AccessToken.getValue().equals(deviceCredentials.getToken())));
                deviceDTO.setToken(deviceCredentials.getToken());
                return deviceDTO;
            })
            .collect(Collectors.toList());

        return deviceDTOS;
    }
}
