package com.romif.securityalarm.client.domain.huawei;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
public class UssdStatusResponse {

    @JacksonXmlProperty(localName = "result")
    private Integer result;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "UssdStatusResponse{" +
                "result=" + result +
                '}';
    }
}
