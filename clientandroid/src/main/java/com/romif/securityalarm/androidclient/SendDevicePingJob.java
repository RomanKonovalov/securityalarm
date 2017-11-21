package com.romif.securityalarm.androidclient;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SendDevicePingJob extends JobService {

    static final String TAG = "SendDevicePingJobTag";

    @Override
    public boolean onStartJob(JobParameters params) {
        WifiReceiver.scheduleJob(getApplicationContext(), params.getExtras().getString("successToken"), params.getExtras().getString("refreshToken"));
        new PingDeviceTask(getApplicationContext()).execute(params.getExtras().getString("successToken"), params.getExtras().getString("refreshToken"));
        Log.d(TAG, "onStartJob");

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob");
        return false;
    }
}

class PingDeviceTask extends AsyncTask<String, Void, Boolean> {
    private Context context;

    public PingDeviceTask(Context context) {
        this.context = context;

    }

    @Override
    protected Boolean doInBackground(String... credentials) {
        Log.d("PingDeviceTask", credentials[0]);
        Log.d("PingDeviceTask", credentials[1]);

       /* try {
            Properties properties = new Properties();
            properties.load(context.getAssets().open("application.properties"));

            HttpAuthentication authHeader = new HttpBasicAuthentication(credentials[0], credentials[1]);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAuthorization(authHeader);
            requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            final String url = properties.getProperty("securityalarm.client.url") + Constants.PING_DEVICE_PATH;
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity httpEntity = new HttpEntity(requestHeaders);

            ResponseEntity<String> token = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

            return HttpStatus.OK.equals(token.getStatusCode());
        } catch (Exception e) {
            Log.e("PingDeviceTask", e.getMessage(), e);
        }*/

        return false;
    }
}
