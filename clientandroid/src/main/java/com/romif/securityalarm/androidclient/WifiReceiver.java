package com.romif.securityalarm.androidclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WifiReceiver extends BroadcastReceiver implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WifiReceiver";

    private GoogleApiClient mGoogleApiClient;

    public static void scheduleJob(Context context, String successToken, String refreshToken) {
        ComponentName serviceComponent = new ComponentName(context, SendDevicePingJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(5 * 1000); // wait at least
        builder.setOverrideDeadline(20 * 1000); // maximum delay
        //builder.setPeriodic(20 * 1000);
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("successToken", successToken);
        bundle.putString("refreshToken", refreshToken);
        builder.setExtras(bundle);
        jobScheduler.schedule(builder.build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancel(0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info != null && info.isConnected()) {

                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                Properties properties = new Properties();
                properties.load(context.getAssets().open("application.properties"));
                final String ssid = properties.getProperty("security.oauth2.client.access-token-uri");

                if (ssid.equals(wifiInfo.getSSID())) {

                }

                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(context)
                            .addApi(Auth.CREDENTIALS_API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                }

                CredentialRequest request = new CredentialRequest.Builder()
                        .setSupportsPasswordLogin(true)
                        .build();

                mGoogleApiClient.connect();

                Auth.CredentialsApi.request(mGoogleApiClient, request).setResultCallback(
                        new ResultCallback<CredentialRequestResult>() {
                            @Override
                            public void onResult(CredentialRequestResult credentialRequestResult) {

                                Status status = credentialRequestResult.getStatus();
                                if (credentialRequestResult.getStatus().isSuccess()) {
                                    // Successfully read the credential without any user interaction, this
                                    // means there was only a single credential and the user has auto
                                    // sign-in enabled.
                                    Credential credential = credentialRequestResult.getCredential();
                                    Log.d(TAG, "User: " + credential.getId());
                                    Notification notification = new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                            .setContentTitle("My notification")
                                            .setContentText("Hello World!")
                                            .build();

                                    NotificationManager mNotificationManager =
                                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    mNotificationManager.notify(333, notification);

                                    /*OAuth2Client client = new OAuth2Client.Builder(credential.getId(), credential.getPassword(), properties.getProperty("security.oauth2.client.client-id"), properties.getProperty("security.oauth2.client.client-secret"), properties.getProperty("security.oauth2.client.access-token-uri")).build();

                                    client.requestAccessToken(new OAuthResponseCallback() {
                                        @Override
                                        public void onResponse(OAuthResponse response) {
                                            if (response.isSuccessful()) {
                                                scheduleJob(context, response.getAccessToken(), response.getRefreshToken());
                                            } else {
                                                OAuthError error = response.getOAuthError();
                                                Log.d(TAG, error.getError());
                                            }
                                        }
                                    });*/


                                } else if (status.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {

                                    // This is most likely the case where the user does not currently
                                    // have any saved credentials and thus needs to provide a username
                                    // and password to sign in.
                                    Log.d(TAG, "Sign in required");

                                } else {
                                    Log.w(TAG, "Unrecognized status code: " + status.getStatusCode());

                                }
                            }
                        }, 30, TimeUnit.SECONDS
                );


            } else if (info != null && !info.isConnected()) {
                cancelJob(context);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "GoogleApiClient connected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "GoogleApiClient is suspended with cause code: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient failed to connect: " + connectionResult);
    }
}
