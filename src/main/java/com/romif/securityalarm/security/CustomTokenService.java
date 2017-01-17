package com.romif.securityalarm.security;

import com.romif.securityalarm.config.JHipsterProperties;
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
    private JHipsterProperties jHipsterProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setTokenStore(tokenStore);
        super.setClientDetailsService(clientDetailsService);
        super.setSupportRefreshToken(true);
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken accessToken = super.createAccessToken(authentication);

        jHipsterProperties.getSecurity().getAuthentication().getUsers().stream()
            .filter(user -> ((User)authentication.getPrincipal()).getUsername().equalsIgnoreCase(user.getLogin()))
            .findFirst()
            .ifPresent(user -> {
                tokenStore.removeAccessToken(accessToken);
                if (accessToken.getRefreshToken() != null) {
                    tokenStore.removeRefreshToken(accessToken.getRefreshToken());
                }
                ((DefaultOAuth2AccessToken)accessToken).setValue(user.getToken());
                ((DefaultOAuth2AccessToken) accessToken).setExpiration(null);
                ((DefaultOAuth2AccessToken) accessToken).setRefreshToken(null);
                tokenStore.storeAccessToken(accessToken, authentication);
            });

        return accessToken;
    }
}
