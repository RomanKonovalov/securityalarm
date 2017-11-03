package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.api.dto.LocationDto;
import com.romif.securityalarm.api.dto.StatusDto;
import com.romif.securityalarm.domain.Location;
import com.romif.securityalarm.domain.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto locationDto);

    LocationDto locationToLocationDto(Location status);
}
