package android.bignerdranch.waps_11;

/**
 *
 *
 * A Class to hold all the data of the wifi ap points
 *
 * */

public class WifiObject {
    private String mSSID;
    private String mBSSID;
    private long mRSSI;
    private long mFrequency;
    private String mDate = "scanDate";

    //empty constructor
    public WifiObject(){}

    //constructor for wifi object class
    public WifiObject(String SSID, String BSSID, long RSSI, long frequency, String date) {
        this.mSSID = SSID;
        this.mBSSID = BSSID;
        this.mRSSI = RSSI;
        this.mFrequency = frequency;
        this.mDate = date;
    }

    //getters and setters
    public String getSSID() {
        return mSSID;
    }

    public void setSSID(String SSID) {
        mSSID = SSID;
    }

    public String getBSSID() {
        return mBSSID;
    }

    public void setBSSID(String BSSID) {
        mBSSID = BSSID;
    }

    public long getRSSI() {
        return mRSSI;
    }

    public void setRSSI(long RSSI) {
        mRSSI = RSSI;
    }

    public long getFrequency() {
        return mFrequency;
    }

    public void setFrequency(long frequency) {
        mFrequency = frequency;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }
}
