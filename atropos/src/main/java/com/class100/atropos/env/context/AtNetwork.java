package com.class100.atropos.env.context;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import androidx.annotation.IntDef;

import com.class100.atropos.generic.AtLog;
import com.class100.atropos.generic.AtRuntime;
import com.class100.atropos.generic.AtTexts;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtNetwork extends AtContextAbility {
    private static final String TAG = "NetworkUtils";

    public static final int NET_UNKNOWN = -2; // 未知网络类型
    public static final int NET_NONE = -1;    // 无网络连接
    public static final int NET_WIFI = 0;     // WiFi网络
    public static final int NET_ETHERNET = 1; // 有线网络
    public static final int MOBILE_2G = 2;    // 2G网络
    public static final int MOBILE_3G = 3;    // 3G网络
    public static final int MOBILE_4G = 4;    // 4G网络
    public static final int MOBILE_5G = 5;    // 5G网络

    @IntDef({NET_UNKNOWN, NET_NONE, NET_WIFI, NET_ETHERNET, MOBILE_2G, MOBILE_3G, MOBILE_4G, MOBILE_5G})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NetType {
    }

    private AtNetwork() {
        throw new UnsupportedOperationException("Tool class cannot be instantiated.");
    }

    /**
     * 是否连接网络
     *
     * @return
     */
    public static boolean isConnected() {
        try {
            Context context = env._app;
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = conn.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            AtLog.e(TAG, "isConnected", e.getMessage());
        }

        return false;
    }

    /**
     * 开始扫描wifi
     */
    public static void startScanWifi() {
        Context context = env._app;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null != wifiManager) {
            wifiManager.startScan();
        }
    }


    /**
     * 获取wifi列表
     */
    public static List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) env._app.getSystemService(Context.WIFI_SERVICE);
        return excludeRepetition(wifiManager.getScanResults());
    }

    /**
     * 保存网络
     */
    @SuppressLint("PrivateApi")
    public static void saveNetworkByConfig(WifiConfiguration config) {
        WifiManager manager = (WifiManager) env._app.getSystemService(Context.WIFI_SERVICE);
        try {
            Method save = AtRuntime.reflectMethod(manager, "save", WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            AtRuntime.reflectInvoke(manager, save, config, null);
        } catch (Exception e) {
            AtLog.e(TAG, "saveNetworkByConfig", e.getMessage());
        }
    }

    /**
     * 忘记网络
     */
    @SuppressLint("PrivateApi")
    public static void forgetNetwork(int networkId) {
        WifiManager manager = (WifiManager) env._app.getSystemService(Context.WIFI_SERVICE);
        try {
            Method forget = AtRuntime.reflectMethod(manager, "forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            AtRuntime.reflectInvoke(manager, forget, networkId, null);
        } catch (Exception e) {
            AtLog.e(TAG, "forgetNetwork", e.getMessage());
        }
    }

    /**
     * 断开连接
     */
    public static boolean disconnectNetwork() {
        WifiManager manager = (WifiManager) env._app.getSystemService(Context.WIFI_SERVICE);
        return null != manager && manager.disconnect();
    }


    /**
     * 获取当前wifi名字
     *
     * @return
     */
    public static String getWifiName() {
        Context context = env._app;
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();

        return wifiInfo.getSSID().replaceAll("\"", "");
    }

    /**
     * 获取wifi加密方式
     */
    public static String getEncrypt(ScanResult scanResult) {
        String result = "";
        String capabilities = scanResult.capabilities;
        if (!AtTexts.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                result = "WPA";
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                result = "WEP";
            }
        }

        return result;
    }

    /**
     * 是否开启wifi，没有的话打开wifi
     */
    public static boolean openWifi() {
        boolean bRet = true;
        Context context = env._app;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null != wifiManager && !wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }

        return bRet;
    }


    public static void connectWifi(String wifiName, String password, String type) {
        WifiManager wifiManager = (WifiManager) env._app.getSystemService(Context.WIFI_SERVICE);
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + wifiName + "\"";
        String psd = "\"" + password + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (type) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            default:
                //无密码
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        // 断开当前连接
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (null != wifiInfo) {
            wifiManager.disableNetwork(wifiInfo.getNetworkId());
            wifiManager.disconnect();
            wifiManager.removeNetwork(wifiInfo.getNetworkId());
        }
        // 清除之前保存的连接数据
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (!AtTexts.isEmpty(i.SSID) && i.SSID.equals(ssid)) {
                wifiManager.disableNetwork(i.networkId);
                wifiManager.disconnect();
                wifiManager.removeNetwork(i.networkId);
                break;
            }
        }
        // 开始连接
        int id = wifiManager.addNetwork(conf);
        wifiManager.enableNetwork(id, true);
    }

    /**
     * 清除保存的WiFi信息
     *
     * @param ssid wifi名
     */
    public static void removeSaveConfig(String ssid) {
        Context context = env._app;
        if (AtTexts.isEmpty(ssid)) {
            return;
        }

        if (!ssid.startsWith("\"")) {
            ssid = "\"" + ssid;
        }
        if (!ssid.endsWith("\"")) {
            ssid = ssid + "\"";
        }

        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = manager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (!AtTexts.isEmpty(i.SSID) && i.SSID.equals(ssid)) {
                manager.disableNetwork(i.networkId);
                manager.disconnect();
                manager.removeNetwork(i.networkId);
                break;
            }
        }
    }

    /**
     * 获取当前正在连接的WIFI信息
     *
     * @return 当前正在连接的WIFI信息
     */
    public static WifiInfo getConnectionInfo() {
        Context context = env._app;
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        return manager.getConnectionInfo();
    }

    /**
     * 排除重复
     *
     * @param scanResults 带处理的数据
     * @return 去重数据
     */
    public static ArrayList<ScanResult> excludeRepetition(List<ScanResult> scanResults) {
        HashMap<String, ScanResult> hashMap = new HashMap<>();
        for (ScanResult scanResult : scanResults) {
            String ssid = scanResult.SSID;
            if (AtTexts.isEmpty(ssid)) {
                continue;
            }

            ScanResult tempResult = hashMap.get(ssid);
            if (null == tempResult) {
                hashMap.put(ssid, scanResult);
                continue;
            }

            if (WifiManager.calculateSignalLevel(tempResult.level, 100) < WifiManager.calculateSignalLevel(scanResult.level, 100)) {
                hashMap.put(ssid, scanResult);
            }
        }
        ArrayList<ScanResult> results = new ArrayList<>();
        for (Map.Entry<String, ScanResult> entry : hashMap.entrySet()) {
            results.add(entry.getValue());
        }

        return results;
    }

    public static String intToIp(int address) {
        return ((address & 0xFF) + "." +
            ((address >>>= 8) & 0xFF) + "." +
            ((address >>>= 8) & 0xFF) + "." +
            ((address >>>= 8) & 0xFF));
    }

    public static String prefixToMask(int length) {
        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int[] maskParts = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        StringBuilder result = new StringBuilder();
        result.append(maskParts[0]);
        for (int i = 1; i < maskParts.length; i++) {
            result.append(".").append(maskParts[i]);
        }

        return result.toString();
    }


    /**
     * 判断是否有网络连接
     *
     * @return true：有网络连接，false：无网络连接
     */
    public static boolean isNetworkConnected() {
        boolean result = false;
        try {
            Context context = env._app;
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            result = info.isAvailable() && info.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 判断 WIFI 网络是否可用
     *
     * @return true：WIFI 可用，false：WIFI 不可用
     */
    public static boolean isWifiConnected() {
        Context context = env._app;
        boolean result = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            //判断 info 对象是否为空 并且类型是否为 WIFI
            result = info.isAvailable() && info.isConnected()
                && ConnectivityManager.TYPE_WIFI == info.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 判断 ETHERNET 网络是否可用
     *
     * @return true：ETHERNET 网络可用，false：ETHERNET 网络不可用
     */
    public static boolean isEthernetConnected() {
        Context context = env._app;
        boolean result = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            //判断 info 对象是否为空 并且类型是否为 ETHERNET
            result = info.isAvailable() && info.isConnected()
                && ConnectivityManager.TYPE_ETHERNET == info.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 判断 MOBILE 网络是否可用
     *
     * @return true：MOBILE 网络可用，false：MOBILE 网络不可用
     */
    public static boolean isMobileConnected() {
        Context context = env._app;
        boolean result = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            //判断 info 对象是否为空 并且类型是否为 MOBILE
            result = info.isAvailable() && info.isConnected()
                && ConnectivityManager.TYPE_MOBILE == info.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取当前的网络类型
     *
     * @return 网络类型：
     * {@link #NET_UNKNOWN 未知网络}, {@link #NET_NONE 无网络},
     * {@link #NET_WIFI WiFi网络}, {@link #NET_ETHERNET 有线网络},
     * {@link #MOBILE_2G 2G网络}, {@link #MOBILE_3G 3G网络},
     * {@link #MOBILE_4G 4G网络}, {@link #MOBILE_5G 5G网络},
     */
    @NetType
    public static int getNetType() {
        Context context = env._app;
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            return NET_NONE;
        }

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // NetworkInfo 对象为空 则代表没有网络
        if (null == networkInfo || !networkInfo.isAvailable() || !networkInfo.isConnectedOrConnecting()) {
            return NET_NONE;
        }

        int nType = networkInfo.getType();
        if (ConnectivityManager.TYPE_WIFI == nType) {
            return NET_WIFI;
        }

        if (ConnectivityManager.TYPE_ETHERNET == nType) {
            return NET_ETHERNET;
        }

        if (ConnectivityManager.TYPE_MOBILE == nType) {
            int nSubType = networkInfo.getSubtype();
            String nSubTypeName = networkInfo.getSubtypeName();
            switch (nSubType) {
                case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2G
                case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2G
                case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2G
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:

                    return MOBILE_2G;

                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3G
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_UMTS:

                    return MOBILE_3G;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return MOBILE_4G;

                    /*
                // todo
                case TelephonyManager.NETWORK_TYPE_NR: // 对应的20 只有依赖为android 10.0才有此属性
                    return MOBILE_5G;
                     */

                default:
                    //中国移动 联通 电信 三种 3G 制式
                    if ("TD-SCDMA".equalsIgnoreCase(nSubTypeName)
                        || "WCDMA".equalsIgnoreCase(nSubTypeName)
                        || "CDMA2000".equalsIgnoreCase(nSubTypeName)) {
                        return MOBILE_3G;
                    }
            }
        }

        return NET_UNKNOWN;
    }
}
