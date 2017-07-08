package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.api.dto.StatusDto;
import com.romif.securityalarm.domain.Status;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    Status statusDtoToStatus(StatusDto statusDto);

    StatusDto statusToStatusDto(Status status);
}
