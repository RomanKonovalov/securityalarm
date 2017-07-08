package com.romif.securityalarm.client;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan
@EnableScheduling
@EnableConfigurationProperties
public class App implements CommandLineRunner {


    public static void main(String[] args) throws Exception {

        SpringApplication app = new SpringApplication(App.class);
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.run(args);

    }

    // Put your logic here.
    @Override
    public void run(String... args) throws Exception {

    }
}

/*public class App
{
    public static void main( String[] args ) throws IOException {

        ResultParser resultParser = new ResultParser();

        GPSdEndpoint gpSdEndpoint = new GPSdEndpoint("localhost", 2947, resultParser);

        gpSdEndpoint.addListener(new IObjectListener() {
            public void handleTPV(TPVObject tpv) {

            }

            public void handleSKY(SKYObject sky) {

            }

            public void handleATT(ATTObject att) {

            }

            public void handleSUBFRAME(SUBFRAMEObject subframe) {

            }

            public void handleDevices(DevicesObject devices) {

            }

            public void handleDevice(DeviceObject device) {

            }
        });
        System.out.println( "Hello World!" );
    }
}*/
