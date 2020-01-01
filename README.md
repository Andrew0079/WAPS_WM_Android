# WAPS_WM_Android

The application scans for WiFi access points in the vicinity of the phone obtains SSID, BSSID, RSSI and frequency.
The app also gets a GPS location of the phone that will save into a database.  
Scanning frequency of the WiFi and GPS can be changed.
Data collected during a WiFi scan will be saved into a real-time database.

The app implements IndoorLocation Sensing; Indoor Location is displayed not saved into a database.

Test phone: Motorola E6 (OS: Android 9)

WiFi Scanner Time Interval: 

WiFi scanning is mainly dependent on the hardware manufacturer - along with maybe some user settings and other apps that are running 
(other apps may request scans for WiFi networks). So there is not a "standard scan time" for WiFi.

The app scans perfectly starting from the lowest time interval, which is five seconds.
However, trying to initiate a scan frequency less the five seconds might do not work as expected. 
The scan will still start, but the results must not be displayed since the scanner does not have time to finish a scan before initiating another one. 

Keep the scanning time at least five seconds or higher.

Git: https://github.com/Andrew0079/WAPS_WM_Android.git
Login Credentials:
        email: test@gmail.com
        password: 123456


