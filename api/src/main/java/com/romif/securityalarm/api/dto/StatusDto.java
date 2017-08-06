package com.romif.securityalarm.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class StatusDto {

    private long id;

    private DeviceState deviceState;

    private float latitude;

    private float longitude;

    private byte[] image;

}
