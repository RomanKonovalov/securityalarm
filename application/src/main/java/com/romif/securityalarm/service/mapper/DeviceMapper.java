package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.domain.Device;
import com.romif.securityalarm.service.dto.DeviceDTO;
import com.romif.securityalarm.service.dto.DeviceManagementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AlarmMapper.class)
public interface DeviceMapper {

    DeviceDTO deviceManagementDTOToDeviceDTO(DeviceManagementDTO deviceManagementDTO);

    @Mapping(target = "name", source = "login")
    @Mapping(target = "authorized", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "secret", ignore = true)
    DeviceManagementDTO deviceToDeviceManagementDTO(Device device);

}
