package ico.ico.util;

import android.net.wifi.ScanResult;

/**
 * Created by Administrator on 2014/12/19.
 */
public class Wifi implements Cloneable {
    /**
     * 名称
     */
    private String ssid;
    /**
     * mac
     */
    private String bssid;
    /**
     * 密码
     */
    private String password;
    /**
     * wifi类型
     */
    private WifiType type;
    /**
     * 当前的ip地址
     */
    private String ip;
    /**
     * 信号强度
     */
    private int level;
    private String capabilities;
    private int frequency;

    public Wifi() {
    }

    public Wifi(ScanResult re) {
        this.setBssid(re.BSSID.toUpperCase());
        this.setSsid(re.SSID);
        if (re.capabilities.indexOf(WifiType.WPA.toString()) != -1) {
            this.type = WifiType.WPA;
        } else if (re.capabilities.indexOf(WifiType.WEP.toString()) != -1) {
            this.type = WifiType.WEP;
        } else {
            this.type = WifiType.NONE;
        }
        this.setCapabilities(re.capabilities);
        this.setLevel(re.level);
        this.setFrequency(re.frequency);
    }

    public String getSsid() {
        return ssid;
    }

    public Wifi setSsid(String ssid) {
        this.ssid = ssid;
        return this;
    }

    public String getBssid() {
        return bssid;
    }

    public Wifi setBssid(String bssid) {
        this.bssid = bssid;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Wifi setPassword(String password) {
        this.password = password;
        return this;
    }

    public WifiType getType() {
        return type;
    }

    public Wifi setType(WifiType type) {
        this.type = type;
        return this;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public Wifi setCapabilities(String capabilities) {
        this.capabilities = capabilities;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Wifi setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public Wifi setLevel(int level) {
        this.level = level;
        return this;
    }

    public int getFrequency() {
        return frequency;
    }

    public Wifi setFrequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    @Override
    public Wifi clone() {
        Wifi wifi = new Wifi();
        wifi.setSsid(this.getSsid());
        wifi.setBssid(this.getBssid());
        wifi.setPassword(this.getPassword());
        wifi.setIp(this.getIp());
        wifi.setType(this.getType());
        return wifi;
    }

    public enum WifiType {
        NONE,
        WPA,
        WEP
    }
}
