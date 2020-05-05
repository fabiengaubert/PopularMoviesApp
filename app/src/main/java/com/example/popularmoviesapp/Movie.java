package com.example.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "movie")
public class Movie implements Parcelable {

    @PrimaryKey
    private int mId;
    @ColumnInfo(name="title")
    private String mTitle;
    @ColumnInfo(name="date")
    private String mDate;
    @ColumnInfo(name="overview")
    private String mOverview;
    @ColumnInfo(name="poster_path")
    private String mPosterPath;
    @ColumnInfo(name="vote_average")
    private double mVoteAverage;
    @Ignore
    private boolean mIsFavourite;
    @Ignore
    private ArrayList<Review> mReviews;
    @Ignore
    private ArrayList<String> mTrailersPaths;

    public Movie(int id, String title, String date, String overview, String posterPath, double voteAverage) {
        mId = id;
        mTitle = title;
        mDate = date;
        mOverview = overview;
        mPosterPath = posterPath;
        mVoteAverage = voteAverage;
    }

    protected Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mDate = in.readString();
        mOverview = in.readString();
        mPosterPath = in.readString();
        mVoteAverage = in.readDouble();
        mIsFavourite = in.readByte() != 0;
        mReviews = in.createTypedArrayList(Review.CREATOR);
        mTrailersPaths = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mDate);
        dest.writeString(mOverview);
        dest.writeString(mPosterPath);
        dest.writeDouble(mVoteAverage);
        dest.writeByte((byte) (mIsFavourite ? 1 : 0));
        dest.writeTypedList(mReviews);
        dest.writeStringList(mTrailersPaths);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null) return false;
        if(!(obj instanceof Movie)) return false;
        return ((Movie)obj).getId()==getId();
    }


    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public boolean isFavourite() { return mIsFavourite; }

    public void setIsFavourite(boolean mIsFavourite) { this.mIsFavourite = mIsFavourite; }

    public ArrayList<Review> getReviews() { return mReviews; }

    public void setReviews(ArrayList<Review> mReviews) { this.mReviews = mReviews; }

    public ArrayList<String> getTrailersPaths() { return mTrailersPaths; }

    public void setTrailersPaths(ArrayList<String> mTrailersPaths) { this.mTrailersPaths = mTrailersPaths; }

    @Override
    public int describeContents() {
        return 0;
    }
}
