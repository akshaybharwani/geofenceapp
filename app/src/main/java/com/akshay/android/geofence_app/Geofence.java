package com.akshay.android.geofence_app;

import java.util.UUID;

/**
 * Created by Akshay on 4/4/2018.
 */

public class Geofence {
    private String mId;
    private String mPlaceName;
    private String mLatitude;
    private String mLongitude;
    private int mRadius;

    public Geofence() {

    }

    public Geofence(String mId, String mPlaceName, String mLatitude, String mLongitude, int mRadius) {
        this.mId = mId;
        this.mPlaceName = mPlaceName;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mRadius = mRadius;
    }


    public String getId() {
        return mId;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setid(String mId) {
        this.mId = mId;
    }

    public void setplaceName(String mPlaceName) {
        this.mPlaceName = mPlaceName;
    }

    public void setlatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void setlongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setradius(int mRadius) {
        this.mRadius = mRadius;
    }
}
