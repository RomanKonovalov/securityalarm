package com.romif.securityalarm.domain.sms;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.Date;

@Data
public class Receipt {

    private String number;
    private DeliveryStatus status;
    private String customID;
    private Date datetime;

}
