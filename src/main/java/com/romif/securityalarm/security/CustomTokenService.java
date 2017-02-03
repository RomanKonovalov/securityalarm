package com.romif.securityalarm.security;

import com.romif.securityalarm.config.JHipsterProperties;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceCredentials;
import com.romif.securityalarm.repository.DeviceCredentialsRepository;
import com.romif.securityalarm.repository.DeviceRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Roman_Konovalov on 1/17/2017.
 */
@Service("CustomTokenService")
@Primary
public class CustomTokenService extends DefaultTokenServices {

    @Inject
    private TokenStore tokenStore;

    @Inject
    private ClientDetailsService clientDetailsService;

    @Inject
    private DeviceRepository deviceRepository;

    @Inject
    private DeviceCredentialsRepository deviceCredentialsRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setTokenStore(tokenStore);
        super.setClientDetailsService(clientDetailsService);
        super.setSupportRefreshToken(true);
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken accessToken = super.createAccessToken(authentication);

        List<Device> devices = deviceRepository.findAll();

        devices.stream()
            .filter(device -> ((User)authentication.getPrincipal()).getUsername().equalsIgnoreCase(device.getLogin()))
            .findFirst()
            .map(device -> deviceCredentialsRepository.findOneByDevice(device))
            .filter(optional -> optional.isPresent())
            .map(optional -> optional.get())
            .ifPresent(deviceCredentials -> {
                tokenStore.removeAccessToken(accessToken);
                if (accessToken.getRefreshToken() != null) {
                    tokenStore.removeRefreshToken(accessToken.getRefreshToken());
                }
                ((DefaultOAuth2AccessToken)accessToken).setValue(deviceCredentials.getToken());
                ((DefaultOAuth2AccessToken) accessToken).setExpiration(null);
                ((DefaultOAuth2AccessToken) accessToken).setRefreshToken(null);
                tokenStore.storeAccessToken(accessToken, authentication);
            });

        return accessToken;
    }
}
