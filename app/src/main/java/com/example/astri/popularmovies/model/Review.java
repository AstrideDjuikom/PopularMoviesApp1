package com.example.astri.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String review_id;
    private String review_author;
    private String review_content;

    public Review (String id, String author,String content){
        this.review_id=id;
        this.review_author=author;
        this.review_content=content;
    }

    protected Review(Parcel in) {
        review_id = in.readString();
        review_author = in.readString();
        review_content = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(review_id);
        parcel.writeString(review_author);
        parcel.writeString(review_content);
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getReview_author() {
        return review_author;
    }

    public void setReview_author(String review_author) {
        this.review_author = review_author;
    }

    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }
}
