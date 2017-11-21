package com.romif.securityalarm.web.websocket;

import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.Image;
import com.romif.securityalarm.domain.User;
import com.romif.securityalarm.repository.ImageRepository;
import com.romif.securityalarm.repository.UserRepository;
import com.romif.securityalarm.web.websocket.dto.DeviceActivityDTO;
import com.romif.securityalarm.web.websocket.dto.DeviceTrackingControlDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import javax.inject.Inject;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Controller
public class DeviceActivityService {

    private static final Logger log = LoggerFactory.getLogger(DeviceActivityService.class);
    private final SimpMessageSendingOperations messagingTemplate;
    @Inject
    private SimpUserRegistry simpUserRegistry;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ImageRepository imageRepository;
    private Map<String, Map<Long, BlockingDeque<Image>>> userImagesMap = new ConcurrentHashMap<>();

    public DeviceActivityService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @SubscribeMapping("/topic/deviceTrackingControl")
    public void getDeviceTrackingControl(@Payload DeviceTrackingControlDTO deviceTrackingControlDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        //sendDeviceActivity(stompHeaderAccessor.getSessionId());

    }

    private boolean isUserSubscribed(String login) {
        return CollectionUtils.isNotEmpty(simpUserRegistry.findSubscriptions(
            (subscription) -> "/user/queue/deviceTracker".equals(subscription.getDestination()) && login.equals(subscription.getSession().getUser().getName())));
    }

    @EventListener
    public void onApplicationEvent(SessionSubscribeEvent event) {

        String login = event.getUser().getName();

        if (userImagesMap.get(login) != null) {
            return;
        }

        userImagesMap.put(login, new HashMap<>());

        Set<Device> devices = userRepository.findOneByLogin(login).map(User::getDevices).orElse(Collections.emptySet());

        for (Device device : devices) {
            if (userImagesMap.get(login).get(device.getId()) == null) {
                userImagesMap.get(login).put(device.getId(), new LinkedBlockingDeque<>());
            }

            new Thread(getFetchImagesJob(login, device)).start();

            new Thread(getSendImagesJob(login, device)).start();
        }


    }

    @NotNull
    private Runnable getSendImagesJob(String login, Device device) {
        return () -> {

            Image previousImage = null;
            long millisToSleep = 1000;
            try {
                do {

                    BlockingDeque<Image> images = userImagesMap.get(login).get(device.getId());
                    Image currentImage = images.pollFirst(60, TimeUnit.SECONDS);

                    if (currentImage == null || simpUserRegistry.getUser(login) == null) {
                        Thread.sleep(100);
                        continue;
                    }

                    for (SimpSession simpSession : simpUserRegistry.getUser(login).getSessions()) {
                        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
                        ha.setSessionId(simpSession.getId());
                        ha.setLeaveMutable(true);

                        DeviceActivityDTO deviceActivityDTO = new DeviceActivityDTO();
                        deviceActivityDTO.setId(device.getId());
                        deviceActivityDTO.setImage(currentImage.getRawImage());

                        messagingTemplate.convertAndSendToUser(simpSession.getId(), "/queue/deviceTracker", deviceActivityDTO, ha.getMessageHeaders());

                    }

                    if (previousImage != null) {
                        millisToSleep = Math.abs(currentImage.getDateTime().toInstant().toEpochMilli() - previousImage.getDateTime().toInstant().toEpochMilli());
                    }

                    previousImage = currentImage;

                    Thread.sleep(Math.min(millisToSleep, 1000));

                } while (isUserSubscribed(login));
            } catch (Exception e) {
                log.error("Error while running SendImagesJob", e);
            }

        };
    }

    @NotNull
    private Runnable getFetchImagesJob(String login, Device device) {
        return () -> {

            ZonedDateTime previousTime = ZonedDateTime.now().minusSeconds(30L);
            try {
                do {
                    ZonedDateTime now = ZonedDateTime.now();
                    List<Image> images = imageRepository.findByDateTimeAfterAndDateTimeBeforeAndStatusCreatedBy(previousTime, now, device.getLogin(), new PageRequest(0, 50, Sort.Direction.ASC, "dateTime"));

                    BlockingDeque<Image> queue = userImagesMap.get(login).get(device.getId());
                    images.forEach(image -> queue.offerLast(image));

                    if (CollectionUtils.isNotEmpty(images)) {
                        previousTime = images.get(images.size() - 1).getDateTime();
                    }

                    Thread.sleep(10000);
                } while (isUserSubscribed(login));

            } catch (Exception e) {
                log.error("Error while running FetchImagesJob", e);
            }

        };
    }

    @EventListener
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        userImagesMap.remove(event.getUser().getName());
    }

    @EventListener
    public void onApplicationEvent(SessionDisconnectEvent event) {
        userImagesMap.remove(event.getUser().getName());
    }
}
