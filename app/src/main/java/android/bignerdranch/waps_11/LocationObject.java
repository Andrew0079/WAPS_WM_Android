package android.bignerdranch.waps_11;

import java.util.Date;

public class LocationObject {
    private double mLongitude;
    private double mLatitude;
    private String mDate;

    public LocationObject() { }

    public LocationObject(double longitude, double latitude, String date) {
        mLongitude = longitude;
        mLatitude = latitude;
        mDate = date;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }
}
