package com.romif.securityalarm.service;

import com.romif.securityalarm.config.ApplicationProperties;
import com.romif.securityalarm.domain.ConfigStatus;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceCredentials;
import com.romif.securityalarm.repository.DeviceCredentialsRepository;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.service.dto.DeviceDTO;
import com.romif.securityalarm.service.dto.DeviceManagementDTO;
import com.romif.securityalarm.service.mapper.DeviceMapper;
import com.romif.securityalarm.service.util.RandomUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final Logger log = LoggerFactory.getLogger(DeviceService.class);

    @Inject
    private JdbcTokenStore tokenStore;

    @Inject
    private ApplicationProperties applicationProperties;

    @Inject
    private DeviceCredentialsRepository deviceCredentialsRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private DeviceRepository deviceRepository;

    @Inject
    private SecurityService securityService;

    @Inject
    private DeviceMapper deviceMapper;

    @Inject
    private SmsTxtlocalService smsService;

    public List<DeviceManagementDTO> getAllDevices(String login) {

        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(applicationProperties.getSecurity().getAuthentication().getOauth().getClientid());

        List<DeviceManagementDTO> deviceDTOS = deviceCredentialsRepository.findAllByDeviceUserLogin(login).stream()
            .map(deviceCredentials -> {
                DeviceManagementDTO deviceDTO = deviceMapper.deviceToDeviceManagementDTO(deviceCredentials.getDevice());
                deviceDTO.setAuthorized(tokens.stream().anyMatch(oAuth2AccessToken -> oAuth2AccessToken.getValue().equals(deviceCredentials.getToken())));
                deviceDTO.setToken(deviceCredentials.getToken());
                deviceDTO.setPauseToken(deviceCredentials.getPauseToken());
                deviceDTO.setSecret(deviceCredentials.getSecret());
                return deviceDTO;
            })
            .collect(Collectors.toList());

        return deviceDTOS;
    }

    public List<DeviceDTO> getAllLoggedDevices(String login) {

        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(applicationProperties.getSecurity().getAuthentication().getOauth().getClientid());

        List<DeviceDTO> deviceDTOS = getAllDevices(login).stream()
            .filter(deviceDTO -> deviceDTO.isAuthorized())
            .map(deviceManagementDTO -> deviceMapper.deviceManagementDTOToDeviceDTO(deviceManagementDTO))
            .collect(Collectors.toList());

        return deviceDTOS;
    }

    public Device createDevice(Device device) {

        String rawPassword = RandomUtil.generatePassword();
        String pauseToken = UUID.randomUUID().toString();
        String secret = RandomStringUtils.randomAlphanumeric(8);

        device.setPassword(passwordEncoder.encode(rawPassword));
        device.setPauseToken(passwordEncoder.encode(pauseToken));

        Device result = deviceRepository.save(device);

        DeviceCredentials deviceCredentials = new DeviceCredentials(null, result, rawPassword, UUID.randomUUID().toString(), pauseToken, secret);

        deviceCredentialsRepository.save(deviceCredentials);

        return result;
    }

    public boolean updateDevice(DeviceDTO deviceDTO) {
        return deviceRepository.findOneById(deviceDTO.getId()).map(device -> {
            device.setPhone(deviceDTO.getPhone());
            device.setApn(deviceDTO.getApn());
            device.setDescription(deviceDTO.getDescription());
            deviceRepository.save(device);
            return true;
        }).orElse(false);
    }

    public void deleteDevice(String login) {
        tokenStore.findTokensByUserName(login).forEach(token ->
            tokenStore.removeAccessToken(token));
        deviceRepository.findOneByLogin(login).ifPresent(device -> {
            deviceRepository.delete(device);
            log.debug("Deleted Device: {}", device);
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

    public CompletableFuture<ConfigStatus> configDevice(String login) {
        Optional<Device> device = deviceRepository.findOneByLogin(login);
        Optional<DeviceCredentials> deviceCredentials = deviceCredentialsRepository.findOneByDeviceLogin(login);
        if (device.isPresent() && deviceCredentials.isPresent()) {
            CompletableFuture<ConfigStatus> future = smsService.sendConfig(device.get(), deviceCredentials.get());
            future.thenApply(configStatus -> {
                device.get().setConfigStatus(configStatus);
                deviceRepository.save(device.get());
                return configStatus;
            });
            return future;
        } else {
            return CompletableFuture.completedFuture(ConfigStatus.NOT_CONFIGURED);
        }
    }

    public boolean logoutDevice(String login) {
        tokenStore.findTokensByUserName(login).forEach(token ->
            tokenStore.removeAccessToken(token));

        return tokenStore.findTokensByUserName(login).isEmpty();

    }
}
