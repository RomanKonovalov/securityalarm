package com.romif.securityalarm.client.domain.huawei;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "response")
public class Hosts {

    @JacksonXmlElementWrapper(localName = "Hosts")
    @JacksonXmlProperty(localName = "Hosts")
    private List<Host> hosts;

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public static class Host {

        @JacksonXmlProperty(localName = "AssociatedSsid")
        private String associatedSsid;

        @JacksonXmlProperty(localName = "MacAddress")
        private String macAddress;

        @JacksonXmlProperty(localName = "ID")
        private String id;

        @JacksonXmlProperty(localName = "IpAddress")
        private String ipAddress;

        @JacksonXmlProperty(localName = "AssociatedTime")
        private String associatedTime;

        @JacksonXmlProperty(localName = "HostName")
        private String hostName;

        public String getAssociatedSsid() {
            return associatedSsid;
        }

        public void setAssociatedSsid(String associatedSsid) {
            this.associatedSsid = associatedSsid;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getAssociatedTime() {
            return associatedTime;
        }

        public void setAssociatedTime(String associatedTime) {
            this.associatedTime = associatedTime;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }
    }
}


