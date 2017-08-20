package com.strike.udpdemo;

/**
 * Created by miaojizhen on 2017/6/20.
 */
public class UdpScanReceiveData {
    private String name;
    private String MAC;
    private String IP;
    private String deviceID;
    private String msgType;

    public UdpScanReceiveData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UdpScanReceiveData that = (UdpScanReceiveData) o;

        if (!MAC.equals(that.MAC)) return false;
        if (!IP.equals(that.IP)) return false;
        return deviceID.equals(that.deviceID);

    }

    @Override
    public int hashCode() {
        int result = MAC.hashCode();
        result = 31 * result + IP.hashCode();
        result = 31 * result + deviceID.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UdpScanReceiveData{" +
                "name='" + name + '\'' +
                ", MAC='" + MAC + '\'' +
                ", IP='" + IP + '\'' +
                ", deviceID='" + deviceID + '\'' +
                ", msgType='" + msgType + '\'' +
                '}';
    }
}