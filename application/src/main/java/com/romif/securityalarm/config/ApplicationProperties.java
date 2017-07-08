package com.romif.securityalarm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Properties specific to JHipster.
 *
 * <p>
 *     Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "jhipster", ignoreUnknownFields = false)
@Getter
public class ApplicationProperties {

    private final Async async = new Async();

    private final Http http = new Http();

    private final Cache cache = new Cache();

    private final Mail mail = new Mail();

    private final Security security = new Security();

    private final Swagger swagger = new Swagger();

    private final Metrics metrics = new Metrics();

    private final CorsConfiguration cors = new CorsConfiguration();

    private final Ribbon ribbon = new Ribbon();

    private final Image image = new Image();

    @Getter
    @Setter
    public static class Async {

        private int corePoolSize = 2;

        private int maxPoolSize = 50;

        private int queueCapacity = 10000;
    }

    @Getter
    @Setter
    public static class Http {

        private final Cache cache = new Cache();

        private String host;

        @Getter
        public static class Cache {

            private int timeToLiveInDays = 1461;

        }
    }

    public static class Cache {
    }

    @Getter
    @Setter
    public static class Mail {

        private String from = "securityalarm@localhost";

        private String baseUrl = "";

    }

    @Getter
    public static class Security {

        private final Authentication authentication = new Authentication();

        private final Sms sms = new Sms();

        @Getter
        public static class Authentication {

            private final Oauth oauth = new Oauth();

            @Getter
            @Setter
            public static class Oauth {

                private String clientid;

                private String secret;

                private int tokenValidityInSeconds = 1800;

            }

        }

        @Getter
        @Setter
        public static class Sms {
            private String url;
            private String apikey;
        }
    }

    @Getter
    @Setter
    public static class Swagger {

        private String title = "securityalarm API";

        private String description = "securityalarm API documentation";

        private String version = "0.0.1";

        private String termsOfServiceUrl;

        private String contactName;

        private String contactUrl;

        private String contactEmail;

        private String license;

        private String licenseUrl;

    }

    @Getter
    public static class Metrics {

        private final Jmx jmx = new Jmx();

        private final Graphite graphite = new Graphite();

        private final Prometheus prometheus = new Prometheus();

        private final Logs logs = new Logs();

        @Getter
        @Setter
        public static class Jmx {

            private boolean enabled = true;

        }

        @Getter
        @Setter
        public static class Graphite {

            private boolean enabled = false;

            private String host = "localhost";

            private int port = 2003;

            private String prefix = "securityalarm";

        }

        @Getter
        @Setter
        public static class Prometheus {

            private boolean enabled = false;

            private String endpoint = "/prometheusMetrics";

        }

        @Getter
        @Setter
        public static class Logs {

            private boolean enabled = false;

            private long reportFrequency = 60;

        }
    }

    private final Logging logging = new Logging();

    @Getter
    public static class Logging {

        private final Logstash logstash = new Logstash();

        @Getter
        @Setter
        public static class Logstash {

            private boolean enabled = false;

            private String host = "localhost";

            private int port = 5000;

            private int queueSize = 512;

        }
    }

    @Getter
    @Setter
    public static class Ribbon {

        private String[] displayOnActiveProfiles;

    }

    @Getter
    @Setter
    public static class Image {

        private String storagePath;

        private long maxLongSide;

    }
}
