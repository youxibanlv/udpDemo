package com.strike.udpdemo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by miaojizhen on 2017/6/8.
 */

public class WifiUtils {

    public static boolean isWifiOpen(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
        }
        return false;
    }

    public static String getIP(Context context) {
        WifiManager wifiService = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo = wifiService.getConnectionInfo();
        return intToIp(wifiinfo.getIpAddress());
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }
}
