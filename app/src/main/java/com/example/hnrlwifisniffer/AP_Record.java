package com.example.hnrlwifisniffer;

import java.util.UUID;

public class AP_Record {

  //ap id
  int id = 0;
  //即记录哪一次搜索产生的数据
  int fp_id;
  //时间
  String datetime;
  //设备名称
  String device;
  //sdk版本
  String sdk_version;
  //空间描述
  String loc_build;
  String loc_floor;
  String loc_room;
  float loc_x;
  float loc_y;
  String loc_z;
  //UTM坐标
  String loc_coordinate;
  //AP信息
  String bssid;
  String ssid;
  String cap;
  int cf0;
  int cf1;
  String c_width;
  int freq;
  int level;
  long timestamp;

  //imu信息
  float acc_x;
  float acc_y;
  float acc_z;
  float gro_x;
  float gro_y;
  float gro_z;
  float mag_x;
  float mag_y;
  float mag_z;

  int flag_upload = 0;

  UUID uuid = UUID.randomUUID();
  UUID fp_uuid = UUID.randomUUID();
}
