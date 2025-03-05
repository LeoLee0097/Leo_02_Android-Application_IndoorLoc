package com.example.hnrlwifisniffer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.util.List;

public class WiFiUtil {

  private static WiFiUtil util;
  private WifiManager WifiManager;
  private WifiInfo WifiInfo;
  private List<ScanResult> WifiList;
  private List<WifiConfiguration> WifiConfiguration;
  private WifiManager.WifiLock WifiLock;

  // 构造器
  private WiFiUtil(Context context) {
    //取得WifiManager对象
    WifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    //取得WifiInfo对象 有没有这句无所谓
    WifiInfo = WifiManager.getConnectionInfo();
  }

  public static WiFiUtil getInstance(Context context) {
    if (util == null) {
      synchronized (WiFiUtil.class) {
        util = new WiFiUtil(context);
      }
    }
    return util;
  }

  public boolean isEnabled() {
    return WifiManager.isWifiEnabled();
  }

  //打开WIFI
  //失效
  public void openWifi() {
    if (!WifiManager.isWifiEnabled()) {
      WifiManager.setWifiEnabled(true);
    }
  }

  //关闭WIFI
  //失效
  public void closeWifi() {
    if (WifiManager.isWifiEnabled()) {
      WifiManager.setWifiEnabled(false);
    }
  }

  // 检查当前WIFI状态
  public int checkState() {
    return WifiManager.getWifiState();
  }

  // 锁定WifiLock
  public void acquireWifiLock() {
    WifiLock.acquire();
  }

  // 解锁WifiLock
  public void releaseWifiLock() {
    // 判断时候锁定
    if (WifiLock.isHeld()) {
      WifiLock.acquire();
    }
  }

  // 创建一个WifiLock
  public void creatWifiLock(String lockName) {
    WifiLock = WifiManager.createWifiLock(lockName);
  }

  // 得到配置好的网络
  public List<WifiConfiguration> getConfiguration() {
    return WifiConfiguration;
  }

  // 指定配置好的网络进行连接
  public void connectConfiguration(int index) {
    // 索引大于配置好的网络索引返回
    if (index > WifiConfiguration.size()) {
      return;
    }
    // 连接配置好的指定ID的网络
    WifiManager.enableNetwork(WifiConfiguration.get(index).networkId, true);
  }

  public void startScan() {
    WifiManager.startScan();
    // 得到扫描结果
    WifiList = WifiManager.getScanResults();
  }

  // 得到网络列表
  public List<ScanResult> getWifiList() {
    return WifiList;
  }

  // 查看扫描结果
  public StringBuilder lookUpScan() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < WifiList.size(); i++) {
      stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
      // 将ScanResult信息转换成一个字符串包
      // 其中把包括：BSSID、SSID、capabilities、frequency、level
      stringBuilder.append((WifiList.get(i)).toString());
      stringBuilder.append("/n");
    }
    return stringBuilder;
  }

  // 得到MAC地址
  @SuppressLint("MissingPermission")
  public String getMacAddress() {
    return (WifiInfo == null) ? "NULL" : WifiInfo.getMacAddress();
  }

  // 得到接入点的BSSID
  public String getBSSID() {
    return (WifiInfo == null) ? "NULL" : WifiInfo.getBSSID();
  }

  // 得到IP地址
  public int getIPAddress() {
    return (WifiInfo == null) ? 0 : WifiInfo.getIpAddress();
  }

  // 得到连接的ID
  public int getNetworkId() {
    return (WifiInfo == null) ? 0 : WifiInfo.getNetworkId();
  }

  // 得到WifiInfo的所有信息包
  public String getWifiInfo() {
    return (WifiInfo == null) ? "NULL" : WifiInfo.toString();
  }

  // 添加一个网络并连接
  public void addNetwork(WifiConfiguration wcg) {
    int wcgID = WifiManager.addNetwork(wcg);
    boolean b = WifiManager.enableNetwork(wcgID, true);
    System.out.println("a--" + wcgID);
    System.out.println("b--" + b);
  }

  public void disconnectWiFiNetWork(int networkId) {
    // 设置对应的wifi网络停用
    WifiManager.disableNetwork(networkId);

    // 断开所有网络连接
    WifiManager.disconnect();
  }
}
