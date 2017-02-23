package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.service.dto.AlarmDTO;
import com.romif.securityalarm.service.dto.DeviceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface AlarmMapper {

    AlarmDTO alarmToAlarmDTO(Alarm alarm);
}
