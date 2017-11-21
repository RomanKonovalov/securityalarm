package com.romif.securityalarm.androidclient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.auth.api.credentials.Credential;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Properties;

public class VerifyCredentialsTask extends AsyncTask<Credential, Void, Boolean> {

    private Context context;

    public VerifyCredentialsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Credential... credentials) {

        try {
            Properties properties = new Properties();
            properties.load(context.getAssets().open("application.properties"));

            OAuth2RestTemplate restTemplate = ResourceConfiguration.restTemplate(properties.getProperty("security.oauth2.client.access-token-uri"), properties.getProperty("security.oauth2.client.client-id"), properties.getProperty("security.oauth2.client.client-secret"), credentials[0].getId(), credentials[0].getPassword());

            OAuth2AccessToken oAuth2AccessToken = restTemplate.getAccessToken();

            return !oAuth2AccessToken.isExpired();
        } catch (Exception e) {
            Log.e("VerifyCredentialsTask", e.getMessage(), e);
        }

        return false;
    }


}