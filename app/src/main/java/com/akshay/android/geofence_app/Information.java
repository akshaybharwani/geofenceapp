package com.akshay.android.geofence_app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akshay on 3/4/2018.
 */

public class Information implements Parcelable {

    private String mId;
    private String mTitle;
    private String mDescription;
    /*private ArrayList<String> mCategories;*/
    private Geofence mGeofence;

    public Information() {

    }

    public Information(String artistId, String title, String description, Geofence geofence/*, ArrayList<String> categories*/) {
        mId = artistId;
        mTitle = title;
        mDescription = description;
        /*mCategories = categories;*/
        mGeofence = geofence;
    }

    public String getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    /*public ArrayList<String> getmCategories() {
        return mCategories;
    }*/

    public Geofence getmGeofence() {
        return mGeofence;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDescription);/*
        dest.writeParcelable(this.mGeofence, flags);*/
    }

    protected Information(Parcel in) {
        this.mId = in.readString();
        this.mTitle = in.readString();
        this.mDescription = in.readString();
        this.mGeofence = in.readParcelable(Geofence.class.getClassLoader());
    }

    public static final Parcelable.Creator<Information> CREATOR = new Parcelable.Creator<Information>() {
        @Override
        public Information createFromParcel(Parcel source) {
            return new Information(source);
        }

        @Override
        public Information[] newArray(int size) {
            return new Information[size];
        }
    };
}
