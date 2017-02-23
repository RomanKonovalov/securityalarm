package com.romif.securityalarm.service.dto;

import com.romif.securityalarm.domain.Device;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * Created by Roman_Konovalov on 1/23/2017.
 */
@Data
@NoArgsConstructor
public class DeviceManagementDTO {

    private Long id;

    private String name;

    @Size(max = 50)
    private String description;

    private AlarmDTO alarm;

    private boolean authorized;

    private String token;

    private String pauseToken;

}
