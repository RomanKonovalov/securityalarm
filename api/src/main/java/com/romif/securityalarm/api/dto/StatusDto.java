package com.romif.securityalarm.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = "images")
@NoArgsConstructor
public class StatusDto {

    private long id;

    private DeviceState deviceState;

    private Integer deviceTemperature;

    private LocationDto location;

    private List<ImageDto> images;

}
