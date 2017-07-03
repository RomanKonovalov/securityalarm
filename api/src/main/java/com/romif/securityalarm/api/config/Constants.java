package com.romif.securityalarm.api.config;
/**
 * Application constants.
 */
public final class Constants {

    public static final String SEND_LOCATION_PATH = "/api/statuses";
    public static final String SEND_IMAGE_PATH = "/api/statuses/{status}/image";
    public static final String PAUSE_ALARM_PATH = "/api/alarms/pause";
    public static final String RESUME_ALARM_PATH = "/api/alarms/resume";
    public static final String HANDLE_RECEIPTS_PATH = "/devices/handleReceipts";

    private Constants() {
    }
}
