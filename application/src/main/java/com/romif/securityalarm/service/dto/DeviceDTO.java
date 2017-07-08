package com.romif.securityalarm.service.dto;

import com.romif.securityalarm.domain.ConfigStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Created by Roman_Konovalov on 1/23/2017.
 */
@Data
@NoArgsConstructor
public class DeviceDTO {

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 50)
    private String description;

    private AlarmDTO alarm;

    @NotBlank
    @Size(max = 50)
    private String apn;

    @NotBlank
    private String phone;

    private ConfigStatus configStatus;

}
