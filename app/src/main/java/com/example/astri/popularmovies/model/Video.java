package com.example.astri.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
    private String video_id;
    private String video_key;
    private String video_name;


    protected Video(Parcel in) {
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    public Video(String video_id, String video_key, String video_name) {
        this.video_id = video_id;
        this.video_key = video_key;
        this.video_name = video_name;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_key() {
        return video_key;
    }

    public void setVideo_key(String video_key) {
        this.video_key = video_key;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }
}
