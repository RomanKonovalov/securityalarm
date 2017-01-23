package com.romif.securityalarm.service.dto;

import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.domain.User;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by Roman_Konovalov on 1/23/2017.
 */
public class DeviceDTO {

    private Long id;

    @Size(max = 50)
    private String name;

    public DeviceDTO() {
    }

    public DeviceDTO(User user) {
        this.id = user.getId();
        this.name = user.getFirstName();
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
}
