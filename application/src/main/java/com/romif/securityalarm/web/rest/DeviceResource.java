package com.romif.securityalarm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.romif.securityalarm.api.config.AuthoritiesConstants;
import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.domain.ConfigStatus;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.sms.DeliveryStatus;
import com.romif.securityalarm.domain.sms.Receipt;
import com.romif.securityalarm.service.DeviceService;
import com.romif.securityalarm.service.SmsTxtlocalService;
import com.romif.securityalarm.service.dto.DeviceDTO;
import com.romif.securityalarm.service.dto.DeviceManagementDTO;
import com.romif.securityalarm.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.beans.PropertyEditorSupport;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Roman_Konovalov on 1/26/2017.
 */
@RestController
@RequestMapping("/api")
public class DeviceResource {

    private final Logger log = LoggerFactory.getLogger(DeviceResource.class);

    @Inject
    private DeviceService deviceService;

    @Inject
    private SmsTxtlocalService smsService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(DeliveryStatus.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(DeliveryStatus.getByCode(text));
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @PostMapping(Constants.HANDLE_RECEIPTS_PATH)
    @Timed
    public ResponseEntity<?> handleReceipt(Receipt receipt) {
        log.debug("REST request to handle Receipt");
        smsService.handleReceipt(receipt);
        return ResponseEntity.ok().build();
        // Receipt(number=375445486323, status=D, customID=, datetime=2017-02-23 21:49:56)
    }

    @GetMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<DeviceManagementDTO>> getDevices(@PathVariable String login) {
        log.debug("REST request to get Devices for : {}", login);
        List<DeviceManagementDTO> deviceDtos = deviceService.getAllDevices(login);

        return new ResponseEntity<>(deviceDtos, HttpStatus.OK);
    }

    @PostMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}/logout")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> logoutDevice(@PathVariable String login) {
        log.debug("REST request to logout Device: {}", login);
        boolean result = deviceService.logoutDevice(login);
        return result ? ResponseEntity.ok().headers(HeaderUtil.createAlert("A Device is logged out with login " + login, login)).build() :
            ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("device", "deviceError", "Error. Device is still logged"))
                .body(null);
    }

    @PostMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}/config")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public CompletableFuture<ConfigStatus> configDevice(@PathVariable String login) {
        log.debug("REST request to config Device: {}", login);
        return deviceService.configDevice(login);
        /*return result ? ResponseEntity.ok().headers(HeaderUtil.createAlert( "Config has been sent to device: " + login, login)).build() :
            ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("device", "deviceError", "Error while sending config"))
                .body(null);*/
    }

    @DeleteMapping("/devices/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteDevice(@PathVariable String login) {
        log.debug("REST request to delete Device: {}", login);
        deviceService.deleteDevice(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("A Device is deleted with identifier " + login, login)).build();
    }

    @GetMapping("/devices")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<DeviceDTO>> getAllLoggedDevices() {
        String login = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        List<DeviceDTO> deviceDtos = deviceService.getAllLoggedDevices(login);

        return new ResponseEntity<>(deviceDtos, HttpStatus.OK);
    }

    @PostMapping("/devices")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> createDevice(@RequestBody Device device) throws URISyntaxException {
        log.debug("REST request to save Device : {}", device);

        Device result = deviceService.createDevice(device);

        return ResponseEntity.created(new URI("/api/devices/" + result.getLogin()))
            .headers(HeaderUtil.createAlert("A Device is created with identifier " + result.getLogin(), result.getLogin()))
            .body(result);
    }

    @PutMapping("/devices")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<?> updateDevice(@RequestBody DeviceDTO device) {
        log.debug("REST request to update Device : {}", device);

        if (deviceService.updateDevice(device)) {
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert("A device is updated with name " + device.getDescription(), device.getDescription()))
                .build();
        } else {
            return ResponseEntity.notFound()
                .headers(HeaderUtil.createAlert("A device is not found with name " + device.getDescription(), device.getDescription()))
                .build();
        }


    }
}
