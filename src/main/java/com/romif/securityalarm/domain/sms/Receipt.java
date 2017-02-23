package com.romif.securityalarm.domain.sms;

import lombok.Data;

@Data
public class Receipt {

    private String number;
    private String status;
    private String customID;
    private String datetime;

}
