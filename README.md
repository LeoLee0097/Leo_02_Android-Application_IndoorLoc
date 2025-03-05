# Android Application -- RSSI Sniffing

## Introduction
This is an Android application for the WAP RSSI collection.

## Typical usage
1. Install the apk file in the folder `bin\app\build\outputs\apk\debug` to your Android phone.
2. Copy the 'server_main_update.py' to your ECS (Elastic Cloud Server).
3. Run the 'server_main_update.py' on your server with:
    ```bash
    nohup python server_main_update.py > nohup_main_[current date].log 2>&1 &
    ```
    where `nohup_main_[current date].log` is the log file for the server. 'nohup' is used to keep the server running even after the terminal is closed. The log file is used to check the status of the server. `'2>&1'` redirects the error message to the log file. `[current date]` is the current date; you should replace it with the current date. For example, if today is 2023-01-01, you should use `nohup_main_230101.log` as the log file name.
4. Run the application on your phone.
5. Restart the server
    ```bash
    ps -ef | grep server_main_update.py
    kill -9 [pid]
    ```
    or 
    ```bash
    sudo lsof -i:5001
    kill -9 [pid]
    ```

## Tencent server
My ECS server is located in Tencent Cloud. The IP address is
```
[Ur IP Address]
```
The username is
```
ubuntu
```
The password is
```
[Ur PWD]
```
Using the following command to connect to the server:
```bash
ssh ubuntu@[Ur IP Address]
```
The program on the server is running on (listening to) port 5001.

## Contributors
+ Kyeong Soo(Joseph) Kim - Team Leader
+ Sihao (Leo) Li - Author and maintainer, until ``Beta 1.2.5`` version
+ Zhe (Tim) Tang - Reporter of bugs and suggestions
