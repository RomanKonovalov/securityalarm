package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.domain.Alarm;
import com.romif.securityalarm.service.dto.AlarmDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlarmMapper {

    AlarmDTO alarmToAlarmDTO(Alarm alarm);
}
