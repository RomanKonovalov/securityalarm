package com.romif.securityalarm.client.domain.huawei;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "request")
public class UssdRequest {

    @JacksonXmlProperty(localName = "content")
    private String content;

    @JacksonXmlProperty(localName = "codeType")
    private String codeType;

    public UssdRequest() {
    }

    public UssdRequest(String content, String codeType) {
        this.content = content;
        this.codeType = codeType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    @Override
    public String toString() {
        return "UssdRequest{" +
                "content='" + content + '\'' +
                ", codeType='" + codeType + '\'' +
                '}';
    }
}
