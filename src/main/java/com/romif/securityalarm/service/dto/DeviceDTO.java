package com.romif.securityalarm.service.dto;

import com.romif.securityalarm.config.Constants;
import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

}
