package com.example.hnrlwifisniffer;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

  //yang
  public boolean b_save_to_local = true;

  public int bt_scan_flg = 1;
  public int bt_save_flg = 1;
  public int bt_export_flg = 1;

  public int AP_num = 0;
  public StringBuilder sb_temp;
  //保存的条目数
  public int ap_saved = 0;
  public int empty_flg = 0;

  public int fp_saved = 0;
  public int fp_count = 0;
  //imu
  public float acc_x;
  public float acc_y;

  //JSONObject aps=new JSONObject();
  public float acc_z;
  public float gro_x;
  public float gro_y;
  public float gro_z;
  public float mag_x;
  public float mag_y;
  public float mag_z;
  public String locb = "0";
  public String locf = "0";
  public String locr = "0";
  public float locx = 0;
  public float locy = 0;
  public String locz = "0";
  public UUID uuid = UUID.randomUUID();
  Vector<AP_Record> aps = new Vector<AP_Record>();
  Vector<AP_Record> fps = new Vector<AP_Record>();
  private SensorManager sensorManager;
  private Sensor accelerometer;
  private SensorEventListener listenera = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float accx = event.values[0];
      float accy = event.values[1];
      float accz = event.values[2];

      acc_x = accx;
      acc_y = accy;
      acc_z = accz;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  };

  private SensorEventListener listenerg = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float gyrox = event.values[0];
      float gyroy = event.values[1];
      float gyroz = event.values[2];

      gro_x = gyrox;
      gro_y = gyroy;
      gro_z = gyroz;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  };
  private SensorEventListener listenerm = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
      float magx = event.values[0];
      float magy = event.values[1];
      float magz = event.values[2];

      mag_x = magx;
      mag_y = magy;
      mag_z = magz;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      if (Environment.isExternalStorageManager()) {
        File internal = new File("/sdcard");
        File[] internalContents = internal.listFiles();
      } else {
        Intent permissionIntent = new Intent(
          Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
        );
        startActivity(permissionIntent);
      }
    }

    String baseDir = Environment
      .getExternalStorageDirectory()
      .getAbsolutePath();
    //String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    String fileName = "ap-rcd.csv";
    String filePath = baseDir + File.separator + fileName;
    File f = new File(filePath);
    if (f.exists() && !f.isDirectory()) f.delete();

    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor sensora = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(
      listenera,
      sensora,
      SensorManager.SENSOR_DELAY_GAME
    );
    Sensor sensorg = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    sensorManager.registerListener(
      listenerg,
      sensorg,
      SensorManager.SENSOR_DELAY_GAME
    );
    Sensor sensorm = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    sensorManager.registerListener(
      listenerm,
      sensorm,
      SensorManager.SENSOR_DELAY_GAME
    );

    //布局随焦点上移
    getWindow()
      .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    //获取可用分辨率
    WindowManager windowManager = getWindow().getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    Point point = new Point();
    display.getSize(point);
    int width = point.x;
    int height = point.y;

    TextView tx2 = findViewById(R.id.textView2);
    tx2.setWidth(width - 24);
    tx2.setHeight((int) (height * 0.33));

    TextView tx7 = findViewById(R.id.textView7);
    TextView tx8 = findViewById(R.id.textView8);

    tx2.setMovementMethod(ScrollingMovementMethod.getInstance());

    Button bt = findViewById(R.id.button);
    Button bt2 = findViewById(R.id.button2);
    Button bt3 = findViewById(R.id.button3);
    Button bt4 = findViewById(R.id.button5);
    Button bt5 = findViewById(R.id.button4);

    TableRow tb = findViewById(R.id.tableRow);
    tb.getBackground().setAlpha(100);

    TextInputEditText tlocb = findViewById(R.id.loc_building);
    TextInputEditText tlocf = findViewById(R.id.loc_floor);
    TextInputEditText tlocr = findViewById(R.id.loc_room);
    TextInputEditText tlocx = findViewById(R.id.loc_x);
    TextInputEditText tlocy = findViewById(R.id.loc_y);
    TextInputEditText tlocz = findViewById(R.id.loc_z);

    //暂时无用了
    int bench = 0;
    if (width < height) {
      bench = width;
    } else {
      bench = height;
    }

    bt.setHeight((int) (height / 15 * 1.5));
    bt.setWidth((int) (width / 10 * 3));
    bt2.setHeight((int) (height / 15 * 1.5));
    bt2.setWidth((int) (width / 10 * 3));
    bt3.setHeight((int) (height / 15 * 1.5));
    bt3.setWidth((int) (width / 10 * 3));
    bt3.setText("UPLOAD");
    //注意这个修改button样式的方法
    bt.setBackgroundColor(getResources().getColor(R.color.lightgreen));
    bt4.setBackgroundColor(getResources().getColor(R.color.lightcoral));
    bt5.setBackgroundColor(getResources().getColor(R.color.lightcoral));

    //scan button
    bt.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //Toast.makeText(MainActivity.this,"press",Toast.LENGTH_LONG).show();
          if (bt_scan_flg == 1) {
            init_scan();
            try {
              sleep(300);
              tx2.setText(AP_num + " APs Found:\n");
              //imu
              tx2.append("\n IMU Info:");
              tx2.append(
                "\n ACC X,Y,Z: [" + acc_x + "," + acc_y + "," + acc_z + "]"
              );
              tx2.append(
                "\n GYRO X,Y,Z: [" + gro_x + "," + gro_y + "," + gro_z + "]"
              );
              tx2.append(
                "\n MAG X,Y,Z: [" + mag_x + "," + mag_y + "," + mag_z + "]\n"
              );
              tx2.append(sb_temp.toString());

              locb = tlocb.getText().toString();
              locf = tlocf.getText().toString();
              locr = tlocr.getText().toString();
              locx = Float.parseFloat(tlocx.getText().toString());
              locy = Float.parseFloat(tlocy.getText().toString());
              locz = tlocz.getText().toString();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    );

    //save button
    bt2.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (bt_save_flg == 1) {
            try {
              sleep(300);
              locb = tlocb.getText().toString();
              locf = tlocf.getText().toString();
              locr = tlocr.getText().toString();
              locx = Float.parseFloat(tlocx.getText().toString());
              locy = Float.parseFloat(tlocy.getText().toString());
              locz = tlocz.getText().toString();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            init_save();
            try {
              sleep(300);
              tx7.setText(String.valueOf(fp_count));
              tx8.setText(String.valueOf(fps.size()));
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    );

    //upload button
    bt3.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (bt_export_flg == 1) {
            init_export();
          }
        }
      }
    );

    //empty button
    bt4.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (true) {
            try {
              init_empty();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            tx7.setText("0");
            tx8.setText("0");
          }
        }
      }
    );

    bt5.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (true) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(
              MainActivity.this
            );
            dialog.setTitle("警告");
            dialog.setMessage("你真的要删库跑路吗?");
            dialog.setNegativeButton(
              "取消",
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
              }
            );

            dialog.setPositiveButton(
              "确定",
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  new Thread() {
                    public void run() {
                      new Thread(
                        new Runnable() {
                          @Override
                          public void run() {
                            int result = HttpClean();
                          }
                        }
                      )
                        .start();
                    }
                  }
                    .start();
                }
              }
            );

            dialog.show();
          }
        }
      }
    );

    new Thread() {
      public void run() {
        try {
          sleep(300);
          if (empty_flg == 1) {
            tx7.setText("#");
            tx8.setText("#");
            empty_flg = 0;
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
      .start();

    det();
  }

  private void init_empty() throws IOException {
    aps_clear();
    fps_clear();
    fp_count = 0;

    int ap_num = fps.size();

    //clear file
    String baseDir = Environment
      .getExternalStorageDirectory()
      .getAbsolutePath();
    //String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    String fileName = "ap-rcd.csv";
    String filePath = baseDir + File.separator + fileName;
    File f = new File(filePath);
    CSVWriter writer;
    new FileWriter(filePath, false).close();
  }

  private void init_scan() {
    Toast.makeText(MainActivity.this, "Scanning...", Toast.LENGTH_LONG).show();
    bt_scan_switch();
  }

  private void bt_scan_switch() {
    if (bt_scan_flg == 0) {
      Toast.makeText(MainActivity.this, "Hold on...", Toast.LENGTH_LONG).show();
    } else {
      bt_scan_flg = 0;
      new Thread() {
        public void run() {
          try {
            sleep(300);
            bt_scan_flg = 1;
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
        .start();
      sniff();
    }
  }

  private void init_save() {
    if (fp_saved == 1) {
      Toast
        .makeText(MainActivity.this, "FP Saved Yet.", Toast.LENGTH_LONG)
        .show();
    } else {
      save();
      Toast
        .makeText(MainActivity.this, "FP Saved Done.", Toast.LENGTH_LONG)
        .show();
    }
  }

  private void save() {
    for (AP_Record ap : aps) {
      fps.add(ap);
    }
    uuid = UUID.randomUUID();
    fp_count += 1;
    fp_saved = 1;
    aps_clear();
  }

  //安卓开发中 主线程不能发送请求 子线程不能更新界面
  private void init_export() {
    if (fps.size() == 0) {
      Toast
        .makeText(MainActivity.this, "No data for upload.", Toast.LENGTH_LONG)
        .show();
    } else {
      export();
    }
  }

  private void export() {
    int nonce = (int) (Math.random() * 10000) + 1;
    Toast.makeText(MainActivity.this, "Upload.", Toast.LENGTH_LONG).show();
    for (int i = 0; i < fps.size(); i++) {
      AP_Record ap = fps.get(i);
      //网络操作线程化
      new Thread() {
        public void run() {
          new Thread(
            new Runnable() {
              @Override
              public void run() {
                Looper.prepare();

                if (ap.flag_upload == 0) {
                  int result;
                  if (b_save_to_local) {
                    try {
                      result = this.save_ap_rcd_to_local(ap, nonce);
                    } catch (IOException e) {
                      throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                      throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                      throw new RuntimeException(e);
                    }
                  } else result = HttpUrlConnectionGet(ap, nonce);
                  ap.flag_upload = 1;
                  if (result == 0) {
                    //成功执行上传 删除现有条目
                    Toast
                      .makeText(
                        MainActivity.this,
                        "Upload done.",
                        Toast.LENGTH_LONG
                      )
                      .show();
                    //fps.remove(ap);
                  }
                }
              }

              private int writeCSVHeader(CSVWriter writer)
                throws ClassNotFoundException {
                Class aClassHandle = Class.forName(
                  "com.example.hnrlwifisniffer.AP_Record"
                );
                //Field[] fields = aClassHandle.getFields();
                Field[] fields = aClassHandle.getDeclaredFields();
                String[] hds = new String[fields.length];
                int i = 0;
                for (Field f : fields) {
                  String name = f.getName();
                  hds[i++] = name;
                }

                writer.writeNext(hds);

                return 1;
              }

              private int save_ap_rcd_to_local(AP_Record ap, int nonce)
                throws IOException, IllegalAccessException, ClassNotFoundException {
                //                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                //                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                //                            intent.setData(uri);
                //                            startActivity(intent);

                String baseDir = Environment
                  .getExternalStorageDirectory()
                  .getAbsolutePath();
                //String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
                String fileName = "ap-rcd.csv";
                String filePath = baseDir + File.separator + fileName;
                File f = new File(filePath);
                CSVWriter writer;

                // File exist
                if (f.exists() && !f.isDirectory()) {
                  FileWriter mFileWriter = new FileWriter(filePath, true);
                  writer = new CSVWriter(mFileWriter);
                } else {
                  //file not exist
                  writer = new CSVWriter(new FileWriter(filePath));
                  this.writeCSVHeader(writer);
                }

                //String[] data = {"Ship Name", "Scientist Name", "...", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").formatter.format(date)};
                String[] data = this.toCSVRcd(ap);
                writer.writeNext(data);

                writer.close();

                return 0;
              }

              private String[] toCSVRcd(AP_Record ap)
                throws IllegalAccessException, ClassNotFoundException {
                Class aClassHandle = Class.forName(
                  "com.example.hnrlwifisniffer.AP_Record"
                );
                //Field[] fields = aClassHandle.getFields();
                Field[] fields = aClassHandle.getDeclaredFields();
                String[] rcd = new String[fields.length];
                int i = 0;
                for (Field f : fields) {
                  String name = f.getName();
                  Object value = f.get(ap);
                  rcd[i++] = value.toString();
                }
                //        for (Field f : getClass().getDeclaredFields()) {
                //            String name = f.getName();
                //            String value = f.get(this);
                //        }

                return rcd;
              }
            }
          )
            .start();
        }
      }
        .start();
    }
  }

  private static final String CSV_SEPARATOR = ",";

  private String[] toCSVRcd(AP_Record ap) throws IllegalAccessException {
    return null;
  }

  private int save_ap_rcd_to_local(AP_Record ap, int nonce)
    throws IOException, IllegalAccessException {
    return 0;
  }

  private int HttpUrlConnectionGet(AP_Record ap, int nonce) {
    HttpURLConnection connection = null;
    try {
      String url = "http://[Ur Address]:5001/msg?";
      connection = (HttpURLConnection) makeurl(url, ap, nonce).openConnection();
      connection.setConnectTimeout(3000);
      connection.setReadTimeout(3000);
      //设置请求方式 GET / POST 一定要大小
      connection.setRequestMethod("GET");
      connection.setDoInput(true);
      connection.setDoOutput(false);
      connection.connect();
      int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        throw new IOException("HTTP error code" + responseCode);
      }
      String result = getStringByStream(connection.getInputStream());
      if (result == ap.bssid) {
        return 0;
      } else {
        return -1;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -2;
  }

  public int HttpClean() {
    HttpURLConnection connection = null;
    try {
      String url = "[Ur IP Address]:5001/order?code=666&";
      URL urld = new URL(url);
      connection = (HttpURLConnection) urld.openConnection();
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);
      //设置请求方式 GET / POST 一定要大小
      connection.setRequestMethod("GET");
      connection.setDoInput(true);
      connection.setDoOutput(false);
      connection.connect();
      int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        throw new IOException("HTTP error code" + responseCode);
      }
      String result = getStringByStream(connection.getInputStream());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -2;
  }

  private URL makeurl(String url, AP_Record ap, int nonce)
    throws MalformedURLException {
    url = apd(url, "bssid", ap.bssid);
    url = apd(url, "ssid", ap.ssid);
    url = apd(url, "fp_id", String.valueOf(ap.fp_id));
    url = apd(url, "id", String.valueOf(ap.id));
    url = apd(url, "device", ap.device);
    url = apd(url, "sdk", ap.sdk_version);
    url = apd(url, "timestamp", String.valueOf(ap.timestamp));
    url = apd(url, "cf0", String.valueOf(ap.cf0));
    url = apd(url, "cf1", String.valueOf(ap.cf1));
    url = apd(url, "cw", String.valueOf(ap.c_width));
    url = apd(url, "level", String.valueOf(ap.level));
    url = apd(url, "freq", String.valueOf(ap.freq));
    url = apd(url, "locb", String.valueOf(ap.loc_build));
    url = apd(url, "locf", String.valueOf(ap.loc_floor));
    url = apd(url, "locr", String.valueOf(ap.loc_room));
    url = apd(url, "locx", String.valueOf(ap.loc_x));
    url = apd(url, "locy", String.valueOf(ap.loc_y));
    url = apd(url, "locz", String.valueOf(ap.loc_z));
    url = apd(url, "accx", String.valueOf(ap.acc_x));
    url = apd(url, "accy", String.valueOf(ap.acc_y));
    url = apd(url, "accz", String.valueOf(ap.acc_z));
    url = apd(url, "grox", String.valueOf(ap.gro_x));
    url = apd(url, "groy", String.valueOf(ap.gro_y));
    url = apd(url, "groz", String.valueOf(ap.gro_z));
    url = apd(url, "magx", String.valueOf(ap.mag_x));
    url = apd(url, "magy", String.valueOf(ap.mag_y));
    url = apd(url, "magz", String.valueOf(ap.mag_z));
    url = apd(url, "uuid", String.valueOf(ap.uuid));
    url = apd(url, "fp_uuid", String.valueOf(ap.fp_uuid));
    url = apd(url, "datetime", String.valueOf(ap.datetime));
    url = apd(url, "nonce", String.valueOf(nonce));
    URL urld = new URL(url);
    return urld;
  }

  private String apd(String url, String key, String value) {
    url += key;
    url += "=";
    url += value;
    url += "&";
    return url;
  }

  private String getStringByStream(InputStream inputStream) {
    Reader reader;
    try {
      reader = new InputStreamReader(inputStream, "UTF-8");
      char[] rawBuffer = new char[512];
      StringBuffer buffer = new StringBuffer();
      int length;
      while ((length = reader.read(rawBuffer)) != -1) {
        buffer.append(rawBuffer, 0, length);
      }
      Toast
        .makeText(MainActivity.this, "Scanning...", Toast.LENGTH_LONG)
        .show();
      return buffer.toString();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  //检测wifi状态
  private void det() {
    //检测wifi开关
    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    int wifiInfo = wifiManager.getWifiState();

    //WifiManager.WIFI_STATE_DISABLING : WIFI网卡正在关闭（0）
    //WifiManager.WIFI_STATE_DISABLED : WIFI网卡不可用（1）
    //WifiManager.WIFI_STATE_ENABLING : WIFI网正在打开（2） （WIFI启动需要一段时间）
    //WifiManager.WIFI_STATE_ENABLED : WIFI网卡可用（3）
    //WifiManager.WIFI_STATE_UNKNOWN : 未知网卡状态

    if (wifiInfo < 3) {
      //尝试打开wifi

      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      dialog.setTitle("请开启 WiFi");
      dialog.setMessage("本应用需要使用 WiFi");
      dialog.setNegativeButton(
        "取消",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            //杀死当前activity 不同oem对安卓fw层改动较大 可能有不同效果
            System.exit(0);
          }
        }
      );
      dialog.setPositiveButton(
        "确定",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            if (!wifiManager.isWifiEnabled()) {
              Intent wi = new Intent(Settings.ACTION_WIFI_SETTINGS);
              startActivity(wi);
              //安卓10之后的应用无法开关wifi 以下代码不起作用
              //wifiManager.setWifiEnabled(true);
            }
          }
        }
      );
      dialog.show();
    }
  }

  public void aps_clear() {
    aps.clear();
  }

  public void fps_clear() {
    fps.clear();
  }

  //执行嗅探
  //安卓9之后 谷歌限制了startscan的使用 功能上仍然可以运作 但是使用次数和频率将受到限制
  public void sniff() {
    aps_clear();
    new Thread() {
      public void run() {
        try {
          sleep(100);
          WifiManager wifiManager = (WifiManager) getSystemService(
            WIFI_SERVICE
          );
          scan();
          sleep(100);
          List<ScanResult> WifiList = wifiManager.getScanResults();
          //解析
          StringBuilder result = dumpScan(WifiList);
          log_console(result);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
      .start();
  }

  //scan动作必须与权限检查同时存在
  private void scan() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      ArrayList<String> permissions = new ArrayList<String>();
      if (
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED
      ) {
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
      }
      if (
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED
      ) {
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
      }
      if (permissions.size() > 0) {
        requestPermissions(
          permissions.toArray(new String[permissions.size()]),
          1234567
        );
      } else {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.startScan();
      }
    } else {
      //无所谓
    }
  }

  public void log_console(StringBuilder sb) {
    sb_temp = sb;
  }

  // 查看扫描结果
  public StringBuilder dumpScan(List<ScanResult> WifiList)
    throws JSONException {
    StringBuilder sb = new StringBuilder();
    AP_num = WifiList.size();

    //tiem scheme
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date curDate = new Date(System.currentTimeMillis());
    //获取当前时间
    String str = formatter.format(curDate);
    sb.append("\nDate Time: ").append(str + "\n");

    for (int i = 0; i < WifiList.size(); i++) {
      //构造字符串
      ScanResult result = (ScanResult) WifiList.get(i);
      sb.append("\nBSSID: ").append(result.BSSID);
      sb.append("\nSSID: ").append(result.SSID);
      sb.append("\nCapabilities: ").append(result.capabilities);

      if (result.channelWidth == 4) {
        sb
          .append("\nCenter Frequency of 1st Segment [MHz]: ")
          .append(result.centerFreq0);
        sb
          .append("\nCenter Frequency of 2nd Segment [MHz]: ")
          .append(result.centerFreq1);
      } else {
        sb.append("\nCenter Frequency [MHz]: ").append(result.centerFreq0);
      }
      String temp;
      switch (result.channelWidth) {
        case 0:
          temp = "20";
          break;
        case 1:
          temp = "40";
          break;
        case 2:
          temp = "80";
          break;
        case 3:
          temp = "160";
          break;
        case 4:
          temp = "160 (80+80)";
          break;
        default:
          temp = Build.VERSION.SDK_INT >= 33 ? "320" : "";
      }
      String channelWidth = temp;
      sb.append("\nChannel Width [MHz]: ").append(channelWidth);
      sb.append("\nFrequency [MHz]: ").append(result.frequency);
      sb.append("\nLevel [dBm]: ").append(result.level);
      sb.append("\nTimestamp [ms]: ").append(result.timestamp);
      sb.append("\n");

      //构造AP记录
      AP_Record ap = new AP_Record();
      //重要
      int ap_temp = ap_saved;
      int fp_temp = fp_count;
      ap_temp += 1;
      fp_temp += 1;

      ap.datetime = str;
      ap.id = ap_temp;
      //实际为nonce
      ap.fp_id = fp_temp;
      ap.fp_uuid = uuid;
      ap.bssid = result.BSSID;
      ap.ssid = result.SSID;
      ap.cap = result.capabilities;
      ap.freq = result.frequency;
      ap.cf0 = result.centerFreq0;
      ap.cf1 = result.centerFreq1;
      ap.c_width = String.valueOf(result.channelWidth);

      ap.level = result.level;
      ap.timestamp = result.timestamp;
      ap.sdk_version = String.valueOf(Build.VERSION.SDK_INT);
      ap.device = Build.MANUFACTURER + " " + Build.MODEL;
      ap.loc_coordinate = ""; // ToDo 未实现, 已经由 locz 代替

      //imu data
      ap.acc_x = acc_x;

      ap.acc_y = acc_y;
      ap.acc_z = acc_z;
      ap.gro_x = gro_x;
      ap.gro_y = gro_y;
      ap.gro_z = gro_z;
      ap.mag_x = mag_x;
      ap.mag_y = mag_y;
      ap.mag_z = mag_z;
      ap.loc_room = locr;

      ap.loc_build = locb;
      ap.loc_floor = locf;
      ap.loc_x = locx;
      ap.loc_y = locy;
      ap.loc_z = locz;
      aps.add(ap);
    }
    fp_saved = 0;
    return sb;
  }
}
