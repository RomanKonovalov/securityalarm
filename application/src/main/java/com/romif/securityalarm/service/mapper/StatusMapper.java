package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.api.dto.StatusDto;
import com.romif.securityalarm.domain.Image;
import com.romif.securityalarm.domain.Status;
import com.romif.securityalarm.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {LocationMapper.class, ImageMapper.class})
public interface StatusMapper {

    Status statusDtoToStatus(StatusDto statusDto);

    StatusDto statusToStatusDto(Status status);

}
