package com.romif.securityalarm.client.domain.huawei;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "request")
public class LogoutRequest {

    @JacksonXmlProperty(localName = "Logout")
    private int logout;

    public LogoutRequest() {
    }

    public LogoutRequest(int logout) {
        this.logout = logout;
    }

    public int getLogout() {
        return logout;
    }

    public void setLogout(int logout) {
        this.logout = logout;
    }
}
