package com.romif.securityalarm.api.config;

import java.util.regex.Pattern;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SEND_LOCATION_PATH = "/api/statuses";
    public static final String PAUSE_ALARM_PATH = "/api/alarms/pause";
    public static final String RESUME_ALARM_PATH = "/api/alarms/resume";
    public static final String PING_DEVICE_PATH = "/api/device/ping";
    public static final String MAC_ADDRESS_PATH = "/api/account/mac_address";
    public static final String MAC_ADDRESS_REGEX = "^[0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}$";
    public static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2})[:-]([0-9A-Fa-f]{2})[:-]([0-9A-Fa-f]{2})[:-]([0-9A-Fa-f]{2})[:-]([0-9A-Fa-f]{2})[:-]([0-9A-Fa-f]{2})$");

    public static final String HANDLE_RECEIPTS_PATH = "/devices/handleReceipts";


    private Constants() {
    }
}
