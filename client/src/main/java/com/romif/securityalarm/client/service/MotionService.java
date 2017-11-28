package com.romif.securityalarm.client.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MotionService {

    private static final String PAUSE_DETECTION_PATH = "/0/detection/pause";
    private static final String START_DETECTION_PATH = "/0/detection/start";
    private static final String STATUS_DETECTION_PATH = "/0/detection/status";
    private static final String RESTART_PATH = "/0/action/restart";
    private static final String CONFIG_SET_PATH = "/0/config/set?{param_name}={value}";
    private static final String CONFIG_GET_PATH = "/0/config/get?query={param_name}";
    private static final String WRITE_CONFIG_PATH = "/0/config/writeyes";

    @Value("${securityalarm.motion.host}")
    private String host;

    private RestTemplate restTemplate = new RestTemplate();

    public boolean pauseDetection() {
        String result = restTemplate.getForObject(host + PAUSE_DETECTION_PATH, String.class);
        return result.contains("Done");
    }

    public boolean startDetection() {
        String result = restTemplate.getForObject(host + START_DETECTION_PATH, String.class);
        return result.contains("Done");
    }

    public boolean statusDetection() {
        String result = restTemplate.getForObject(host + STATUS_DETECTION_PATH, String.class);
        return result.contains("ACTIVE");
    }

    public boolean restart() {
        String result = restTemplate.getForObject(host + RESTART_PATH, String.class);
        return true;
    }

    public boolean setSnapshotInterval(int interval) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("param_name", "snapshot_interval");
        uriVariables.put("value", interval);
        String result = restTemplate.getForObject(host + CONFIG_SET_PATH, String.class, uriVariables);
        return result.contains("Done");
    }

    public boolean setThreshold(int interval) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("param_name", "threshold");
        uriVariables.put("value", interval);
        String result = restTemplate.getForObject(host + CONFIG_SET_PATH, String.class, uriVariables);
        return result.contains("Done");
    }

    public boolean setConfig(String property, String value) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("param_name", property);
        uriVariables.put("value", value);
        String result = restTemplate.getForObject(host + CONFIG_SET_PATH, String.class, uriVariables);
        return result.contains("Done");
    }

    public String getConfig(String property) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("param_name", property);
        String result = restTemplate.getForObject(host + CONFIG_GET_PATH, String.class, uriVariables);

        Matcher matcher = Pattern.compile(property + "\\s=\\s(\\w+).*\\R.*\\R.*").matcher(result);

        return matcher.matches() ? matcher.group(1) : "";
    }

    public boolean writeConfig() {
        String result = restTemplate.getForObject(host + WRITE_CONFIG_PATH, String.class);
        return result.contains("Done");
    }
}
