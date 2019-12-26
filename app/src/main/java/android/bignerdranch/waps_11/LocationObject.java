package android.bignerdranch.waps_11;

/**Locaion class to save all the data
 * after getting collecting the information of the current location*/

public class LocationObject {
    private double mLongitude;
    private double mLatitude;
    private String mDate;

    //empty constructor
    public LocationObject() { }

    //constructor
    public LocationObject(double longitude, double latitude, String date) {
        mLongitude = longitude;
        mLatitude = latitude;
        mDate = date;
    }

    //getters and setters
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
