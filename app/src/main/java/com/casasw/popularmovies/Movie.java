package com.casasw.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Junior on 26/08/2016.
 */
public class Movie implements Parcelable {
    private Integer id;
    private String title;
    private String poster;
    private String thumbNail;
    private String overview;
    private String voteAvg;
    private String releaseDate;

    public Movie(Integer id, String title, String poster,  String thumbNail, String overview,
                 String voteAvg, String releaseDate) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.thumbNail = thumbNail;
        this.overview = overview;
        this.voteAvg = voteAvg;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster  = in.readString();
        thumbNail = in.readString();
        overview = in.readString();
        voteAvg = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(thumbNail);
        dest.writeString(overview);
        dest.writeString(voteAvg);
        dest.writeString(releaseDate);
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

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAvg() {
        return voteAvg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPoster() {
        return poster;
    }
}
