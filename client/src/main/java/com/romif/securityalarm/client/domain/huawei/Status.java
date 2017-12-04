package com.romif.securityalarm.client.domain.huawei;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
public class Status {

    @JacksonXmlProperty(localName = "RoamingStatus")
    private String RoamingStatus;

    @JacksonXmlProperty(localName = "CurrentServiceDomain")
    private String CurrentServiceDomain;

    @JacksonXmlProperty(localName = "BatteryStatus")
    private String BatteryStatus;

    @JacksonXmlProperty(localName = "TotalWifiUser")
    private String TotalWifiUser;

    @JacksonXmlProperty(localName = "CurrentWifiUser")
    private String CurrentWifiUser;

    @JacksonXmlProperty(localName = "SignalStrength")
    private String SignalStrength;

    @JacksonXmlProperty(localName = "simlockStatus")
    private String simlockStatus;

    @JacksonXmlProperty(localName = "SimStatus")
    private String SimStatus;

    @JacksonXmlProperty(localName = "SignalIcon")
    private String SignalIcon;

    @JacksonXmlProperty(localName = "BatteryLevel")
    private String BatteryLevel;

    @JacksonXmlProperty(localName = "WanIPAddress")
    private String WanIPAddress;

    @JacksonXmlProperty(localName = "ServiceStatus")
    private String ServiceStatus;

    @JacksonXmlProperty(localName = "PrimaryDns")
    private String PrimaryDns;

    @JacksonXmlProperty(localName = "WifiStatus")
    private String WifiStatus;

    @JacksonXmlProperty(localName = "SecondaryDns")
    private String SecondaryDns;

    @JacksonXmlProperty(localName = "ConnectionStatus")
    private String ConnectionStatus;

    @JacksonXmlProperty(localName = "CurrentNetworkType")
    private String CurrentNetworkType;

    public String getRoamingStatus() {
        return RoamingStatus;
    }

    public void setRoamingStatus(String RoamingStatus) {
        this.RoamingStatus = RoamingStatus;
    }

    public String getCurrentServiceDomain() {
        return CurrentServiceDomain;
    }

    public void setCurrentServiceDomain(String CurrentServiceDomain) {
        this.CurrentServiceDomain = CurrentServiceDomain;
    }

    public String getBatteryStatus() {
        return BatteryStatus;
    }

    public void setBatteryStatus(String BatteryStatus) {
        this.BatteryStatus = BatteryStatus;
    }

    public String getTotalWifiUser() {
        return TotalWifiUser;
    }

    public void setTotalWifiUser(String TotalWifiUser) {
        this.TotalWifiUser = TotalWifiUser;
    }

    public String getCurrentWifiUser() {
        return CurrentWifiUser;
    }

    public void setCurrentWifiUser(String CurrentWifiUser) {
        this.CurrentWifiUser = CurrentWifiUser;
    }

    public String getSignalStrength() {
        return SignalStrength;
    }

    public void setSignalStrength(String SignalStrength) {
        this.SignalStrength = SignalStrength;
    }

    public String getSimlockStatus() {
        return simlockStatus;
    }

    public void setSimlockStatus(String simlockStatus) {
        this.simlockStatus = simlockStatus;
    }

    public String getSimStatus() {
        return SimStatus;
    }

    public void setSimStatus(String SimStatus) {
        this.SimStatus = SimStatus;
    }

    public String getSignalIcon() {
        return SignalIcon;
    }

    public void setSignalIcon(String SignalIcon) {
        this.SignalIcon = SignalIcon;
    }

    public String getBatteryLevel() {
        return BatteryLevel;
    }

    public void setBatteryLevel(String BatteryLevel) {
        this.BatteryLevel = BatteryLevel;
    }

    public String getWanIPAddress() {
        return WanIPAddress;
    }

    public void setWanIPAddress(String WanIPAddress) {
        this.WanIPAddress = WanIPAddress;
    }

    public String getServiceStatus() {
        return ServiceStatus;
    }

    public void setServiceStatus(String ServiceStatus) {
        this.ServiceStatus = ServiceStatus;
    }

    public String getPrimaryDns() {
        return PrimaryDns;
    }

    public void setPrimaryDns(String PrimaryDns) {
        this.PrimaryDns = PrimaryDns;
    }

    public String getWifiStatus() {
        return WifiStatus;
    }

    public void setWifiStatus(String WifiStatus) {
        this.WifiStatus = WifiStatus;
    }

    public String getSecondaryDns() {
        return SecondaryDns;
    }

    public void setSecondaryDns(String SecondaryDns) {
        this.SecondaryDns = SecondaryDns;
    }

    public String getConnectionStatus() {
        return ConnectionStatus;
    }

    public void setConnectionStatus(String ConnectionStatus) {
        this.ConnectionStatus = ConnectionStatus;
    }

    public String getCurrentNetworkType() {
        return CurrentNetworkType;
    }

    public void setCurrentNetworkType(String CurrentNetworkType) {
        this.CurrentNetworkType = CurrentNetworkType;
    }

    @Override
    public String toString() {
        return "ClassPojo [RoamingStatus = " + RoamingStatus + ", CurrentServiceDomain = " + CurrentServiceDomain + ", BatteryStatus = " + BatteryStatus + ", TotalWifiUser = " + TotalWifiUser + ", CurrentWifiUser = " + CurrentWifiUser + ", SignalStrength = " + SignalStrength + ", simlockStatus = " + simlockStatus + ", SimStatus = " + SimStatus + ", SignalIcon = " + SignalIcon + ", BatteryLevel = " + BatteryLevel + ", WanIPAddress = " + WanIPAddress + ", ServiceStatus = " + ServiceStatus + ", PrimaryDns = " + PrimaryDns + ", WifiStatus = " + WifiStatus + ", SecondaryDns = " + SecondaryDns + ", ConnectionStatus = " + ConnectionStatus + ", CurrentNetworkType = " + CurrentNetworkType + "]";
    }
}
