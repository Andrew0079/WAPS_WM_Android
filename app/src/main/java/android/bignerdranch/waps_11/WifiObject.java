package android.bignerdranch.waps_11;

public class WifiObject {
    private String SSID;
    private String BSSID;
    private long RSSI;
    private long mFrequency;
    private String mDate;

    public WifiObject(){}

    public WifiObject(String SSID, String BSSID, long RSSI, long frequency, String date) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.RSSI = RSSI;
        this.mFrequency = frequency;
        this.mDate = date;


    }

    public String getDate() { return mDate; }

    public void setDate(String date) { mDate = date; }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public long getRSSI() {
        return RSSI;
    }

    public void setRSSI(long RSSI) {
        this.RSSI = RSSI;
    }

    public long getFrequency() {
        return mFrequency;
    }

    public void setLocation(long frequency ) { this.mFrequency = frequency; }

}
