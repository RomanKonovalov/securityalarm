package com.romif.securityalarm.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class ImagesDTO {

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    @NonNull
    private byte[] start;

    @NonNull
    private byte[] end;

}
