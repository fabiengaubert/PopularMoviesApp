package com.example.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie")
public class Movie implements Parcelable {

    @PrimaryKey
    //TODO should we autogerenate the ID (autoGenerate = true)
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mDate);
        dest.writeString(mOverview);
        dest.writeString(mPosterPath);
        dest.writeDouble(mVoteAverage);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
