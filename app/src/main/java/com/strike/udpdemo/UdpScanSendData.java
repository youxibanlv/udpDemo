package com.strike.udpdemo;

/**
 * Created by miaojizhen on 2017/6/20.
 */

public class UdpScanSendData {

    private String IP;
    private String port;
    private String msgType;

    public UdpScanSendData(String IP) {
        this.IP = IP;
        this.port = HttpConstance.PORT;
        this.msgType = HttpConstance.MSG_DEV_SCAN;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
