package com.romif.securityalarm.service;

import com.romif.securityalarm.config.JHipsterProperties;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceCredentials;
import com.romif.securityalarm.repository.DeviceCredentialsRepository;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.service.dto.DeviceDTO;
import com.romif.securityalarm.service.util.RandomUtil;
import com.romif.securityalarm.web.rest.DeviceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final Logger log = LoggerFactory.getLogger(DeviceService.class);

    @Inject
    private JdbcTokenStore tokenStore;

    @Inject
    private JHipsterProperties jHipsterProperties;

    @Inject
    private DeviceCredentialsRepository deviceCredentialsRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private DeviceRepository deviceRepository;

    @Inject
    private SecurityService securityService;

    @Inject
    private SmsService smsService;

    public List<DeviceDTO> getAllDevices(String login) {

        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(jHipsterProperties.getSecurity().getAuthentication().getOauth().getClientid());

        List<DeviceDTO> deviceDTOS = deviceCredentialsRepository.findAllByDeviceUserLogin(login).stream()
            .map(deviceCredentials -> {
                DeviceDTO deviceDTO = new DeviceDTO(deviceCredentials.getDevice());
                deviceDTO.setAuthorized(tokens.stream().anyMatch(oAuth2AccessToken -> oAuth2AccessToken.getValue().equals(deviceCredentials.getToken())));
                deviceDTO.setToken(deviceCredentials.getToken());
                return deviceDTO;
            })
            .collect(Collectors.toList());

        return deviceDTOS;
    }

    public List<DeviceDTO> getAllLoggedDevices(String login) {

        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(jHipsterProperties.getSecurity().getAuthentication().getOauth().getClientid());

        List<DeviceDTO> deviceDTOS = getAllDevices(login).stream()
            .filter(deviceDTO -> deviceDTO.isAuthorized())
            .collect(Collectors.toList());

        return deviceDTOS;
    }

    public Device createDevice(Device device) {

        String rawPassword = RandomUtil.generatePassword();

        device.setPassword(passwordEncoder.encode(rawPassword));

        Device result = deviceRepository.save(device);

        DeviceCredentials deviceCredentials = new DeviceCredentials(result, rawPassword, UUID.randomUUID().toString());

        deviceCredentialsRepository.save(deviceCredentials);

        return result;
    }

    public void deleteDevice(String login) {
        tokenStore.findTokensByUserName(login).forEach(token ->
            tokenStore.removeAccessToken(token));
        deviceRepository.findOneByLogin(login).ifPresent(device -> {
            deviceRepository.delete(device);
            log.debug("Deleted Вevice: {}", device);
        });
    }

    public boolean loginDevice(String login) {
        Optional<DeviceCredentials> deviceCredentials = deviceCredentialsRepository.findOneByDeviceLogin(login);

        if (deviceCredentials.isPresent()) {
            return securityService.authenticate(deviceCredentials.get());
        } else {
            return false;
        }

    }

    public boolean configDevice(String login) {
        Optional<Device> device = deviceRepository.findOneByLogin(login);
        if (device.isPresent()) {
            return smsService.sendConfig(device.get());
        } else {
            return false;
        }
    }

    public boolean logoutDevice(String login) {
        tokenStore.findTokensByUserName(login).forEach(token ->
            tokenStore.removeAccessToken(token));

        return tokenStore.findTokensByUserName(login).isEmpty();

    }
}
