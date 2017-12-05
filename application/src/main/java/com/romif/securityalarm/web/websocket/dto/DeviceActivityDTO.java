package com.romif.securityalarm.web.websocket.dto;


import com.romif.securityalarm.domain.Location;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeviceActivityDTO {

    private Long id;
    private byte[] image;
    private Location location;
    private BigDecimal balance;
    private BigDecimal traffic;

}
