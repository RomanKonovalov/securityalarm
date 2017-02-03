package com.romif.securityalarm.service;

import com.romif.securityalarm.config.JHipsterProperties;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.DeviceCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Roman_Konovalov on 1/26/2017.
 */
@Service
public class SecurityService {

    private final Logger log = LoggerFactory.getLogger(SecurityService.class);

    @Inject
    private TokenEndpoint tokenEndpoint;

    @Inject
    private JdbcTokenStore tokenStore;

    @Inject
    private JHipsterProperties jHipsterProperties;

    public boolean authenticate(DeviceCredentials deviceCredentials) {
        try {
            final Map<String, String> authorizationParameters = new HashMap<>();
            authorizationParameters.put("username", deviceCredentials.getDevice().getLogin());
            authorizationParameters.put("password", deviceCredentials.getRawPassword());
            authorizationParameters.put("grant_type", "password");
            authorizationParameters.put("scope", "read write");
            authorizationParameters.put("client_id", jHipsterProperties.getSecurity().getAuthentication().getOauth().getClientid());
            authorizationParameters.put("client_secret", jHipsterProperties.getSecurity().getAuthentication().getOauth().getSecret());

            final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("Admin"));

            final User userPrincipal = new User(jHipsterProperties.getSecurity().getAuthentication().getOauth().getClientid(), "", true, true, true, true, authorities);

            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities) ;


            final OAuth2AccessToken token = tokenEndpoint.postAccessToken(authenticationToken, authorizationParameters).getBody();

            final Authentication authResult = tokenStore.readAuthentication(token);
            authResult.setAuthenticated(true);

            SecurityContextHolder.getContext().setAuthentication(authResult);

            return true;
        } catch (Exception e) {
            log.error("Can't authenticate Device: " + deviceCredentials.getDevice().getLogin(), e);
            return false;
        }

    }
}
