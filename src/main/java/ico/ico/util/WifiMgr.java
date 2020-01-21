package ico.ico.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.text.TextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fmm on 2014/12/19 0019.
 */
public class WifiMgr {

    /**
     * {@link SharedPreferences}
     */
    public final static String SP_NAME = "wifi";
    public final static String SP_TAG_PWD = "pwd";
    // 保存密码的文件地址
    private static String wifiPwdPath = "/data/misc/wifi/wpa_supplicant.conf";
    // 密码保存文件中的节点名称
    private static String ssidName = "ssid";
    private static String pskName = "psk";
    /**
     * 从配置中读取出来的密码集合
     * key--BSSID
     * value-password
     */
    private static HashMap<String, String> pwdMap = new HashMap<String, String>();


    /**
     * 初始化管理器
     *
     * @param context
     */
    public static void init(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        //密码对
        String json = sp.getString(SP_TAG_PWD, "");
        try {
            HashMap<String, String> map = new ObjectMapper().readValue(json, new TypeReference<HashMap<String, String>>() {
            });
            WifiMgr.setPwdMap(map);
        } catch (Exception e) {
//            e.printStackTrace();
            log.e("初始化密码对，Exception：" + e.toString(), WifiMgr.class.getSimpleName(), "init");
        }
    }


    /**
     * 搜索wifi访问点，返回搜索到的访问点集合
     *
     * @param context
     * @return
     * @throws RemoteException
     */
    public static List<Wifi> getScanResult(Context context) throws RemoteException {
        // 获取保存的所有wifi密码
//        HashMap<String, String> pwdMap = getWifisPwd(context);
        // 获取wifi管理器
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 获取搜索结果
        wifiMgr.startScan();
        List<ScanResult> tmpList = wifiMgr.getScanResults();
        if (tmpList == null) {
            return null;
        }
        List<Wifi> list = new ArrayList<Wifi>();
        for (ScanResult re : tmpList) {
            Wifi wifi = new Wifi(re);
            wifi.setPassword(pwdMap.get(re.SSID));
            list.add(wifi);
        }
        return list;
    }

    /**
     * 搜索wifi访问点，返回指定mac地址的wifi
     *
     * @param context
     * @return
     * @throws RemoteException
     */
    public static Wifi getScanResult(Context context, String bssid) throws RemoteException {
        // 获取保存的所有wifi密码
//        HashMap<String, String> pwdMap = getWifisPwd(context);
        Wifi wifi = null;
        // 获取wifi管理器
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 获取搜索结果
        wifiMgr.startScan();
        List<ScanResult> tmpList = wifiMgr.getScanResults();
        List<Wifi> list = new ArrayList<>();
        for (ScanResult re : tmpList) {
            if (TextUtils.equals(re.BSSID, bssid)) {
                wifi = new Wifi(re);
                wifi.setPassword(pwdMap.get(re.BSSID));
                break;
            }
        }
        return wifi;
    }


    /**
     * 开启wifi搜索，在结果中筛选指定的wifi，返回
     *
     * @param context
     * @param wifi
     * @return
     */
    public static Boolean isScaned(Context context, Wifi wifi)
            throws RemoteException {
        // 获取搜索结果
        List<Wifi> tmpList = WifiMgr.getScanResult(context);
        // 筛选为设备的wifi点
        for (Wifi tmpWifi : tmpList) {
            if (TextUtils.equals(tmpWifi.getBssid(), wifi.getBssid())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 读取密码配置文件，解析配置文件，返回ssid，pwd的键值对集合
     *
     * @param context
     * @return String
     * @throws RemoteException
     */
    public static HashMap<String, String> getWifisPwd(Context context)
            throws RemoteException {
        HashMap<String, String> pwdMap = new HashMap<String, String>();
        File file = new File(wifiPwdPath);
        // 判断目录
        if ((!file.getParentFile().exists()) || (!file.exists())) {
//            log.i("密码配置文件无法获取！", WifiMgr.class.getSimpleName(), "getWifisPwd");
            return pwdMap;
        }
        StringBuffer sb = new StringBuffer();
        // 创建流读取
        try {
            // 通过缓存读取
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
            // 循环读取，存入sb中
            while (true) {
                byte[] buffer = new byte[1024];
                int len = input.read(buffer);
                if (len == -1) {
                    break;
                }
                sb.append(new String(buffer, 0, len, "UTF-8"));
            }
        } catch (Exception e) {
            log.e("在获取已配置WIFI的密码时，读取密码信息文件时异常,Exception:" + e.toString(), WifiMgr.class.getSimpleName(), "getWifisPwd");
            return pwdMap;
        }
        /*** 开始分析 ***/
        String key = "";
        String value = "";
        // 清空pwdmap
        pwdMap.clear();
        while (sb.indexOf("network") != -1) {
            // 获取每一个网络连接配置的开头和结尾的索引位置
            int start = sb.indexOf("network={");
            int end = sb.indexOf("}");
            // 截取网络连接配置部分的字符串
            String config = sb.substring(start, end);
            // 截掉从0开始一直到end的位置
            sb.delete(0, end);
            // 分析刚才截取的配置字符串,取出其中的ssid和psk(pwd)
            // 头，尾，获取ssid
            int ssid_index = config.indexOf(ssidName);
            int ssid_end = config.indexOf("\n");
            key = config.substring(ssid_index + ssidName.length(), ssid_end)
                    .trim();
            // 重新处理
            config = config.substring(ssid_end);
            // 头，尾，获取pwd
            int psk_index = config.indexOf(pskName);
            int psk_end = config.indexOf("\n");
            value = config.substring(psk_index + pskName.length(), psk_end)
                    .trim();
            // 存入地图
            pwdMap.put(key, value);
        }
        return pwdMap;
    }

    /**
     * 打开WIFI连接
     *
     * @param context
     * @return
     */
    public static boolean enable(Context context) {
        // 获取wifi管理器
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.setWifiEnabled(true);
    }

    /**
     * 关闭WIFI连接
     *
     * @param context
     * @return
     */
    public static boolean disable(Context context) {
        // 获取wifi管理器
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.setWifiEnabled(false);
    }

    /**
     * 连接指定的wifi
     *
     * @param context
     * @return
     * @throws RemoteException
     */
    public static Boolean connectWifi(Context context, Wifi wifi) {
        // 获取wifi管理器
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            // 获取已存在的相同SSID的网络配置ID
            List<Integer> netIds = getExistsNetwork(context, wifi.getBssid());
            // log.e("ico", "netIds" + netIds.toString());
            // 移除已存在的相同SSID的网络配置
            for (Integer id : netIds) {
                wifiMgr.removeNetwork(id);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            log.ew("在连接WIFI时，获取已有的网络配置时异常，Exception:" + e.toString(), WifiMgr.class.getSimpleName(), "connectWifi");
        }
        // 构建网络配置对象
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.allowedAuthAlgorithms.clear();
        wifiConfig.allowedGroupCiphers.clear();
        wifiConfig.allowedKeyManagement.clear();
        wifiConfig.allowedPairwiseCiphers.clear();
        wifiConfig.allowedProtocols.clear();
        // 设置SSID
        wifiConfig.SSID = "\"" + wifi.getSsid() + "\"";
        if (wifi.getType().equals(Wifi.WifiType.WEP)) {
            wifiConfig.wepKeys[0] = "\"" + wifi.getPassword() + "\"";
            wifiConfig.hiddenSSID = true;
            wifiConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.wepTxKeyIndex = 0;
            wifiConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
        } else if (wifi.getType().equals(Wifi.WifiType.WPA)) {
            wifiConfig.preSharedKey = "\"" + wifi.getPassword() + "\"";
            wifiConfig.hiddenSSID = true;

            wifiConfig.status = WifiConfiguration.Status.ENABLED;

            wifiConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            // for WPA
            // wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            // for WPA2
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_EAP);
            wifiConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
        } else {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.wepTxKeyIndex = 0;
        }
        int netId = wifiMgr.addNetwork(wifiConfig);
        boolean flag = wifiMgr.enableNetwork(netId, true);
        // log.e("ico", "flag-------------------------------->" + flag);
        return flag;
    }

    /**
     * 获取已存在的指定SSID的网络配置
     *
     * @param context
     * @param bssid
     * @return
     */
    public static List<Integer> getExistsNetwork(Context context, String bssid) {
        // 获取wifi管理器
        WifiManager wifiMgr = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 获取已经配置的所有网络
        List<WifiConfiguration> configuredNetworks = wifiMgr
                .getConfiguredNetworks();
        List<Integer> netIds = new ArrayList<Integer>();
        for (WifiConfiguration tmp : configuredNetworks) {
            if ((tmp.BSSID != null) && (TextUtils.equals(tmp.BSSID, bssid))) {
                netIds.add(tmp.networkId);
            }
        }
        return netIds;
    }

    /**
     * 获取wifi状态
     * <p>
     * {@link WifiManager#getWifiState()}
     */
    public static int getWifiState(Context context) {
        // 获取wifi管理器
        WifiManager wifiMgr = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wifiMgr.getWifiState();
    }

    /** 获取当前连接的Wifi信息 */
    public static Wifi getCurrWifi(Context context) {
        // 获取wifi管理器
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        Wifi wifi = new Wifi();
        String ssid = wifiInfo.getSSID();
        if (ssid != null) {
            ssid = ssid.startsWith("\"") ? ssid.substring(1) : ssid;
            ssid = ssid.endsWith("\"") ? ssid.substring(0, ssid.length() - 1) : ssid;
        }
        wifi.setSsid(ssid);
        wifi.setBssid(wifiInfo.getBSSID());
        wifi.setPassword(pwdMap.get(wifi.getSsid()));
        int ipAdd = wifiInfo.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", ipAdd & 0xff, (ipAdd >> 8) & 0xff, (ipAdd >> 16) & 0xff, (ipAdd >> 24) & 0xff);
        wifi.setIp(ip);
        return wifi;
    }

    /**
     * 当前是否连接任意热点
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        Wifi wifi = WifiMgr.getCurrWifi(context);
        if (wifi != null && !TextUtils.equals(wifi.getSsid(), "<unknown ssid>") && !TextUtils.equals(wifi.getSsid(), "0x")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取当前连接的Wifi信息
     *
     * @param context
     * @return
     */
    public static Boolean isConnect(Context context, Wifi wifi) {
        Wifi _wifi = WifiMgr.getCurrWifi(context);
        // 判断是否已连接上指定的Wifi
        if ((_wifi != null) && (!TextUtils.isEmpty(_wifi.getBssid())) && (TextUtils.equals(_wifi.getBssid(), wifi.getBssid())) && (!TextUtils.isEmpty(_wifi.getIp())) && (!TextUtils.equals(_wifi.getIp(), "0.0.0.0"))) {
            return true;
        }
        return false;
    }


    /**
     * 获得手机当前的ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIp(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        int ip = info.getIpAddress();
        if (ip == 0) {
            return null;
        }
        return Common.int2Ip(ip);
    }

    public static HashMap<String, String> getPwdMap() {
        return pwdMap;
    }

    public static void setPwdMap(HashMap<String, String> pwdMap) {
        WifiMgr.pwdMap.clear();
        WifiMgr.pwdMap.putAll(pwdMap);
    }

    /**
     * 记住wifi密码
     *
     * @param context
     * @param wifi
     */
    public static void addPassword(Context context, Wifi wifi) {
        String flag = WifiMgr.pwdMap.put(wifi.getSsid(), wifi.getPassword());
        savePwd(context, pwdMap);
    }


    /**
     * 保存当前密码对
     *
     * @param context
     * @param pwdMap
     */
    public static void savePwd(Context context, HashMap<String, String> pwdMap) {
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            String json = new ObjectMapper().writeValueAsString(pwdMap);
            sp.edit().putString(SP_TAG_PWD, json).commit();
        } catch (Exception e) {
//            e.printStackTrace();
            log.e("无法保存密码对，Exception：" + e.toString(), WifiMgr.class.getSimpleName(), "setPwdMapASave");
        }
    }
}
