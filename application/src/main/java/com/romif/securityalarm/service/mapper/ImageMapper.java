package com.romif.securityalarm.service.mapper;

import com.romif.securityalarm.api.dto.ImageDto;
import com.romif.securityalarm.api.dto.LocationDto;
import com.romif.securityalarm.domain.Image;
import com.romif.securityalarm.domain.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    Image imageDtoToImage(ImageDto imageDto);

    ImageDto imageToImageDto(Image image);
}
