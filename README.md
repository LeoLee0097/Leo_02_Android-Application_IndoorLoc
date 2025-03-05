# Android Application -- RSSI Sniffing

This is an Android application for the WAP RSSI fingerprint collection for indoor localization database construction.

## Instruction of cloud ver

1. Download and compile code using Android Studio.
2. Install the apk file in the folder `bin\app\build\outputs\apk\debug` to your Android phone.
3. Copy the 'server_main_update.py' to your ECS (Elastic Cloud Server).
4. Run the 'server_main_update.py' on your server with:

    ```bash
    nohup python server_main_update.py > nohup_main_[current date].log 2>&1 &
    ```

    where `nohup_main_[current date].log` is the log file for the server. 'nohup' is used to keep the server running even after the terminal is closed. The log file is used to check the status of the server. `'2>&1'` redirects the error message to the log file. `[current date]` is the current date; you should replace it with the current date. For example, if today is 2025-03-01, you should use `nohup_main_250301.log` as the log file name.
5. Run the application on your phone.
6. Restart the server

    ```bash
    ps -ef | grep server_main_update.py
    kill -9 [pid]
    ```

    or

    ```bash
    sudo lsof -i:5001
    kill -9 [pid]
    ```

## Toy example on using Tencent server

My ECS server is located in Tencent Cloud. The IP address is

```bash
[Ur IP Address]
```

The username is

```bash
[e.g., ubuntu]
```

The password is

```bash
[Ur PWD]
```

Use the following command to connect to the server:

```bash
ssh ubuntu@[Ur IP Address]
```

The program on the server is running on (listening to) ``port 5001``.

## Instructions of local ver

1. Install ``./loc_ver.apk``.

## Contributors

+ Kyeong Soo(Joseph) Kim - Team Leader & original developer
+ Sihao (Leo) Li - Developer and maintainer until ``Beta 1.2.5`` version
+ Zhe (Tim) Tang - Reporter of bugs and suggestions

## Citation

```bibtex
@misc{li_2025_leo_02_androidapplication_indoorloc,
  author       = {Li, Sihao and Kim, Kyeong Soo and Tang, Zhe},
  month        = {03},
  publisher    = {GitHub},
  title        = {LAndroid-Application_IndoorLoc},
  url          = {https://github.com/LeoLee0097/Leo_02_Android-Application_IndoorLoc/blob/main/README.md},
  year         = {2025},
  organization = {GitHub}
}
```
