package com.romif.securityalarm.client.service;

import com.romif.securityalarm.api.dto.ImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class VideoService {

    private final Logger log = LoggerFactory.getLogger(VideoService.class);

    @Value("${securityalarm.pictures.path}")
    private String path;

    @Autowired
    private ApplicationEventPublisher publisher;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());

    private Pattern pattern = Pattern.compile("\\d\\d-(\\d{14})-(\\d\\d)");
    private Pattern patternSnapshot = Pattern.compile("\\d\\d-(\\d{14})-snapshot");

    public List<ImageDto> getImages() throws IOException {

        return Files.list(Paths.get(path))
                .filter(p -> !p.endsWith("lastsnap.jpg"))
                .limit(5)
                .map(p -> {
                    try {
                        byte[] file = Files.readAllBytes(p);
                        Files.delete(p);
                        ImageDto imageDto = new ImageDto();
                        imageDto.setRawImage(file);

                        String fileName = p.getFileName().toString();
                        Matcher matcher = pattern.matcher(fileName);

                        if (matcher.find()) {
                            ZonedDateTime dateTime = ZonedDateTime.parse(matcher.group(1), formatter);
                            dateTime = dateTime.plusNanos(Integer.parseInt(matcher.group(2)) * 10000000 + 1000000);
                            imageDto.setDateTime(dateTime);

                            publisher.publishEvent(new DeviceMotionEvent(this));
                        } else {
                            matcher = patternSnapshot.matcher(fileName);
                            if (matcher.find()) {
                                ZonedDateTime dateTime = ZonedDateTime.parse(matcher.group(1), formatter);
                                imageDto.setDateTime(dateTime);
                            }
                        }

                        return imageDto;
                    } catch (IOException e) {
                        log.error("Error while taking image", e);
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    static class DeviceMotionEvent extends ApplicationEvent {
        public DeviceMotionEvent(Object source) {
            super(source);
        }
    }

}
