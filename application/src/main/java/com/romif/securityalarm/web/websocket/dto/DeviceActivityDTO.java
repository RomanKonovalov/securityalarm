package com.romif.securityalarm.web.websocket.dto;

import com.romif.securityalarm.api.dto.LocationDto;
import lombok.Data;

@Data
public class DeviceActivityDTO {

    private Long id;
    private byte[] image;
    private LocationDto location;

}
