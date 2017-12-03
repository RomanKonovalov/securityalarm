package com.romif.securityalarm.client.config;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    private Constants() {
    }

    public class Modem {

        public static final String HOST = "http://192.168.1.1";

        public static final String MONITORING_STATUS_PATH = "/api/monitoring/status";

        public static final String LOGIN_PATH = "/api/user/login";

        public static final String LOGOUT_PATH = "/api/user/logout";

        public static final String SEND_USSD_PATH = "/api/ussd/send";

        public static final String USSD_STATUS_PATH = "/api/ussd/status";

        public static final String GET_USSD_PATH = "/api/ussd/get";

        public static final String HOST_LIST_PATH = "/api/wlan/host-list";

        private Modem() {
        }

    }
}
