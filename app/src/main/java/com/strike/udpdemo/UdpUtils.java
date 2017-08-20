package com.strike.udpdemo;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;


public class UdpUtils {

    private DatagramSocket datagramSocket;



    private static UdpUtils udpUtils;

    private UdpUtils() {
    }

    public static UdpUtils getInstance() {
        if (udpUtils == null) {
            udpUtils = new UdpUtils();
        }
        return udpUtils;
    }

    public void listener(Handler handler,String localIp,String deviceId){
        if (datagramSocket != null && !datagramSocket.isClosed()){
            datagramSocket.close();
        }
        waitData(handler,localIp,deviceId);
    }

    private void waitData(final Handler handler, final String localIp, final String deviceId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (handler != null) {
                    handler.sendEmptyMessage(HttpConstance.SCANING);
                }
                byte[] data = new byte[1024 * 4];
                datagramSocket = null;
                DatagramPacket dp;
                try {
                    datagramSocket = new DatagramSocket(null);
                    datagramSocket.setReuseAddress(true);
                    datagramSocket.bind(new InetSocketAddress(Integer.parseInt(HttpConstance.PORT)));
                    datagramSocket.setSoTimeout(HttpConstance.DEFAULT_UDP_TIMEOUT);
                    dp = new DatagramPacket(data, data.length);
                    while (!datagramSocket.isClosed()){
                        datagramSocket.receive(dp);
                        if (dp != null){
                            String rsp = new String(dp.getData(), dp.getOffset(), dp.getLength());
                            Log.e("接收到 ==", rsp);
                            if (!TextUtils.isEmpty(rsp)){
                                handler.obtainMessage(HttpConstance.SCAN_SUCCESS,rsp).sendToTarget();
                                UdpScanSendData sendData = new Gson().fromJson(rsp, UdpScanSendData.class);
                                if (sendData != null){
                                    String ip = sendData.getIP();
                                    String msgType = sendData.getMsgType();
                                    if (!TextUtils.isEmpty(msgType) && msgType.equals(HttpConstance.MSG_DEV_SCAN)){
                                        UdpScanReceiveData da = new UdpScanReceiveData();
                                        da.setDeviceID(deviceId);
                                        da.setIP(localIp);
                                        da.setMsgType("MSG_WTP_ONLINE_NOTIFICATION_REQ");
                                        da.setName(deviceId);
                                        da.setMAC(deviceId);
                                        send(ip,da);
                                        handler.obtainMessage(HttpConstance.SCAN_COMPLETE,
                                                new Gson().toJson(da)).sendToTarget();
                                    }
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (datagramSocket != null){
                        datagramSocket.close();
                        handler.obtainMessage(HttpConstance.SCAN_FAILD).sendToTarget();
                    }
                }
            }
        }).start();
    }

    private void send(final String targetIp, final UdpScanReceiveData data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket();
                    socket.setSoTimeout(HttpConstance.DEFAULT_UDP_TIMEOUT);
                    Gson gson = new Gson();
                    String dataStr = gson.toJson(data);
                    byte[] dataByte = dataStr.getBytes();
                    InetAddress ipBroad = InetAddress.getByName(targetIp);
                    DatagramPacket packet = new DatagramPacket(dataByte,dataByte.length,ipBroad,2017);
                    packet.setData(dataByte);
                    socket.send(packet);
                    Log.e("发送数据 ==", dataStr);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (socket != null){
                        socket.close();
                    }
                }
            }
        }).start();
    }

    public void search(String deviceModel, final UdpScanSendData sendData, Handler handler) {
        //发送数据
        send(sendData);
        // 监听来信
        receive(sendData, handler, deviceModel);
    }

    private void send(final UdpScanSendData sendData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket hostSocket = null;
                try {
                    hostSocket = new DatagramSocket();
                    Gson gson = new Gson();
                    hostSocket.setSoTimeout(HttpConstance.DEFAULT_UDP_TIMEOUT);
                    String req = gson.toJson(sendData);
                    Log.e("UDP req", req);
                    byte[] data = req.getBytes();
                    InetAddress ipBroad = InetAddress.getByName("255.255.255.255");
                    DatagramPacket packet = new DatagramPacket(data, data.length, ipBroad, Integer.parseInt(HttpConstance.PORT));
                    packet.setData(data);
                    //发送数据
                    hostSocket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (hostSocket != null) {
                        hostSocket.close();
                        hostSocket = null;
                    }
                }
            }
        }).start();
    }

    private void receive(final UdpScanSendData sendData, final Handler handler, final String deviceModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (handler != null) {
                    handler.sendEmptyMessage(HttpConstance.SCANING);
                }
                byte[] data = new byte[1024 * 4];
                datagramSocket = null;
                DatagramPacket dp;
                Set<UdpScanReceiveData> set = new HashSet<>();
                try {
                    datagramSocket = new DatagramSocket(null);
                    datagramSocket.setReuseAddress(true);
                    datagramSocket.bind(new InetSocketAddress(Integer.parseInt(HttpConstance.PORT)));
                    datagramSocket.setSoTimeout(HttpConstance.DEFAULT_UDP_TIMEOUT);
                    dp = new DatagramPacket(data, data.length);
                    while (!datagramSocket.isClosed()) {
                        datagramSocket.receive(dp);
                        if (dp != null) {
                            String devIp = dp.getAddress().getHostAddress();
                            if (!sendData.getIP().equals(devIp)) {
                                String rsp = new String(dp.getData(), dp.getOffset(), dp.getLength());
                                Log.e("接收到 ==", rsp);
                                if (!TextUtils.isEmpty(rsp)) {
                                    UdpScanReceiveData rspData = new Gson().fromJson(rsp, UdpScanReceiveData.class);
                                    if (rspData != null) {
                                        String type = rspData.getDeviceID().substring(2, 3);
                                        if (HttpConstance.MODEL_RE.equals(deviceModel) && type.equals("2")) {
                                            set.add(rspData);
                                            if (handler != null) {
                                                handler.obtainMessage(HttpConstance.SCAN_SUCCESS, set).sendToTarget();
                                            }
                                        } else if (HttpConstance.MODEL_AP.equals(deviceModel) && type.equals("3")) {
                                            set.add(rspData);
                                            if (handler != null) {
                                                handler.obtainMessage(HttpConstance.SCAN_SUCCESS, set).sendToTarget();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (handler != null) {
                        if (set.size() > 0) {
                            handler.obtainMessage(HttpConstance.SCAN_COMPLETE, set).sendToTarget();
                        } else {
                            handler.obtainMessage(HttpConstance.SCAN_FAILD).sendToTarget();
                        }
                    }
                    if (datagramSocket != null) {
                        datagramSocket.close();
                        datagramSocket = null;
                    }
                }
            }
        }).start();
    }

    public void close() {
        try {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            datagramSocket = null;
        }
    }
}
