package com.romif.securityalarm.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class SystemService {

    private final Logger log = LoggerFactory.getLogger(SystemService.class);

    private static final String CPU_TEMP_CMD = "cat /sys/class/thermal/thermal_zone0/temp";

    public Integer getDeviceTemperature() {

        String result = "";
        BufferedReader in = null;

        try {
            Runtime r = Runtime.getRuntime();

            Process p = r.exec(CPU_TEMP_CMD);
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
            }

        } catch (IOException e) {
            log.error("Can't get CPU temperature", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("Can't close inputStream", e);
                }
            }
        }

        return result.matches("\\d+") ? Integer.parseInt(result) : null;

    }
}
