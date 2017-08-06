package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.api.dto.StatusDto;
import com.romif.securityalarm.domain.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    Status statusDtoToStatus(StatusDto statusDto);

    @Mapping(target = "image", ignore = true)
    StatusDto statusToStatusDto(Status status);
}
