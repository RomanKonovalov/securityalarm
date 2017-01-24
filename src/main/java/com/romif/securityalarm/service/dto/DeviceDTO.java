package com.romif.securityalarm.service.dto;

import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.User;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by Roman_Konovalov on 1/23/2017.
 */
public class DeviceDTO {

    private Long id;

    private String name;

    @Size(max = 50)
    private String description;

    private AlarmDTO alarm;

    public DeviceDTO() {
    }

    public DeviceDTO(Device device) {
        this.id = device.getId();
        this.name = device.getLogin();
        this.description = device.getDescription();
        if (device.getAlarm() != null) {
            this.alarm = new AlarmDTO(device.getAlarm());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AlarmDTO getAlarm() {
        return alarm;
    }

    public void setAlarm(AlarmDTO alarm) {
        this.alarm = alarm;
    }
}
