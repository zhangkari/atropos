package com.class100.atropos.env.context;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.class100.atropos.generic.AtTexts;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;

public class AtDevices extends AtContextAbility {
    /**
     * 获取Device Id
     *
     * @return
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static String getTelephonyDeviceId() {
        Context context = env._app;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("HardwareIds") String deviceId = telephonyManager.getDeviceId();
            return deviceId;
        }
        return "";
    }

    /**
     * 通过系统接口，获取Wifi Mac地址，适用于6.0以下版本
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getWifiMacAddressLegend() {
        Context context = env._app;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }

    /**
     * 通过扫描网络接口，获取Wifi Mac地址，适用于6.0及以上版本
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static String getWifiMacAddress() {
        StringBuilder wifiMacAddressBuild = new StringBuilder();
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (Exception e) {
            return "";
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface anInterface = interfaces.nextElement();
            if (!"wlan0".equals(anInterface.getName())) {
                //测试发现wlan0才是正确的Wifi Mac地址
                continue;
            }
            byte[] address;
            try {
                address = anInterface.getHardwareAddress();
            } catch (Exception e) {
                continue;
            }
            if (address == null || address.length == 0) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            for (byte b : address) {
                builder.append(String.format("%02X:", b));
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            wifiMacAddressBuild.append(anInterface.getName()).append(" -> ").append(builder).append("\n");
        }
        if (wifiMacAddressBuild.length() > 0) {
            wifiMacAddressBuild.deleteCharAt(wifiMacAddressBuild.length() - 1);
        }
        return wifiMacAddressBuild.toString();
    }


    /**
     * 通过分析IP，获取Wifi Mac地址，适用于6.0及以上版本
     *
     * @return
     * @throws SocketException
     */
    public static String getWifiMacAddressByIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {

        }

        return strMacAddr;
    }

    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 获取Android Id
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取Build的部分信息
     *
     * @return
     */
    public static String getBuildInfo() {
        //这里选用了几个不会随系统更新而改变的值
        return Build.BRAND + "/" +
                Build.PRODUCT + "/" +
                Build.DEVICE + "/" +
                Build.ID + "/" +
                Build.VERSION.INCREMENTAL;
    }

    /**
     * 最终方案，获取设备ID
     *
     * @return
     */
    public static String getDeviceUUID() {
        Context context = env._app;
        String uuid = loadDeviceUUID(context);
        if (AtTexts.isEmpty(uuid)) {
            uuid = buildDeviceUUID(context);
            saveDeviceUUID(context, uuid);
        }
        return uuid;
    }

    private static String buildDeviceUUID(Context context) {
        String androidId = getAndroidId(context);
        if (!"9774d56d682e549c".equals(androidId)) {
            Random random = new Random();
            androidId = Integer.toHexString(random.nextInt())
                    + Integer.toHexString(random.nextInt())
                    + Integer.toHexString(random.nextInt());
        }
        return new UUID(androidId.hashCode(), getBuildInfo().hashCode()).toString();
    }

    private static void saveDeviceUUID(Context context, String uuid) {
        context.getSharedPreferences("device_uuid", Context.MODE_PRIVATE)
                .edit()
                .putString("uuid", uuid)
                .apply();
    }

    @Nullable
    private static String loadDeviceUUID(Context context) {
        return context.getSharedPreferences("device_uuid", Context.MODE_PRIVATE)
                .getString("uuid", null);
    }
}
