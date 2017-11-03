package com.romif.securityalarm.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

@Data
@ToString(exclude = "rawImage")
@NoArgsConstructor
public class ImageDto {

    private byte[] rawImage;

    private ZonedDateTime dateTime;
}
