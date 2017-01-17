package com.romif.securityalarm.config;

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
    private TokenEndpoint tokenEndpoint;

    @Inject
    private JdbcTokenStore tokenStore;

    @Inject
    private JHipsterProperties jHipsterProperties;

    @EventListener(ContextRefreshedEvent.class)
    public void authenticateDefaultUsers() {
        log.info("Authenticating users ...");
        jHipsterProperties.getSecurity().getAuthentication().getUsers().forEach(this::authenticate);
        log.info("Authenticating users done");
    }

    private void authenticate(JHipsterProperties.Security.Authentication.User user) {
        try {
            final Map<String, String> authorizationParameters = new HashMap<>();
            authorizationParameters.put("username", user.getLogin());
            authorizationParameters.put("password", user.getPassword());
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
        } catch (Exception e) {
            log.error("Can't authenticate user: " + user.getLogin(), e);
        }

    }
}
