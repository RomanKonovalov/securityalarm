package com.romif.securityalarm.web.websocket;

import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.Image;
import com.romif.securityalarm.domain.Location;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.repository.DeviceRepository;
import com.romif.securityalarm.repository.ImageRepository;
import com.romif.securityalarm.repository.StatusRepository;
import com.romif.securityalarm.repository.UserRepository;
import com.romif.securityalarm.service.dto.LocationDTO;
import com.romif.securityalarm.web.websocket.dto.DeviceActivityDTO;
import com.romif.securityalarm.web.websocket.dto.DeviceTrackingControlDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import javax.inject.Inject;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
public class DeviceActivityService {

    private static final Logger log = LoggerFactory.getLogger(DeviceActivityService.class);
    private final SimpMessageSendingOperations messagingTemplate;
    @Inject
    private SimpUserRegistry simpUserRegistry;
    @Inject
    private DeviceRepository deviceRepository;
    @Inject
    private ImageRepository imageRepository;
    @Inject
    private StatusRepository statusRepository;

    public DeviceActivityService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @SubscribeMapping("/topic/deviceTrackingControl")
    public void getDeviceTrackingControl(@Payload DeviceTrackingControlDTO deviceTrackingControlDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        //sendDeviceActivity(stompHeaderAccessor.getSessionId());

    }

    private Set<SimpSubscription> getSubscriptions(String login) {
        return simpUserRegistry.findSubscriptions(
            (subscription) -> "/user/queue/deviceTracker".equals(subscription.getDestination()) && login.equals(subscription.getSession().getUser().getName()));
    }

    @Scheduled(fixedRate = 20000)
    public void sendDeviceActivities() {
        simpUserRegistry.findSubscriptions(
            (subscription) -> "/user/queue/deviceTracker".equals(subscription.getDestination()))
            .forEach(simpSubscription -> {
                simpSubscription.getId();
                sendDeviceActivity(simpSubscription);
            });
    }

    @EventListener
    public void onApplicationEvent(SessionSubscribeEvent event) {
        String login = event.getUser().getName();
        Executors.newSingleThreadScheduledExecutor().schedule(() -> getSubscriptions(login).forEach(this::sendDeviceActivity), 10, TimeUnit.MILLISECONDS);
    }

    private void sendDeviceActivity(SimpSubscription simpSubscription) {
        String login = simpSubscription.getSession().getUser().getName();

        List<Device> devices = deviceRepository.findAllByUserLogin(login);
        for (Device device : devices) {
            Image image = imageRepository.findFirstByStatusCreatedByAndDateTimeIsNotNullOrderByDateTimeDesc(device.getLogin());
            Optional<Location> location = statusRepository.findFirstByCreatedByOrderByCreatedDateDesc(device.getLogin()).map(Status::getLocation);
            DeviceActivityDTO deviceActivityDTO = new DeviceActivityDTO();
            deviceActivityDTO.setId(device.getId());
            if (image != null) {
                deviceActivityDTO.setImage(image.getRawImage());
            }
            deviceActivityDTO.setBalance(device.getBalance());
            deviceActivityDTO.setTraffic(device.getTraffic());
            if (location.isPresent()) {
                deviceActivityDTO.setLocation(location.get());
            }

            SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            ha.setSessionId(simpSubscription.getSession().getId());
            ha.setLeaveMutable(true);

            messagingTemplate.convertAndSendToUser(simpSubscription.getSession().getId(), "/queue/deviceTracker", deviceActivityDTO, ha.getMessageHeaders());
        }
    }

}
