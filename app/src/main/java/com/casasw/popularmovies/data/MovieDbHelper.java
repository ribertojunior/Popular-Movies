package com.casasw.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for the movies list
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_DATABASE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_LIST + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSITION + " INTEGER NOT NULL, " +

                "UNIQUE ("+ MovieContract.MovieEntry.COLUMN_MOVIE_LIST+", "+
                MovieContract.MovieEntry.COLUMN_POSITION +") ON CONFLICT REPLACE)";

        sqLiteDatabase.execSQL(SQL_DATABASE_MOVIE_TABLE);

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MovieContract.FavoritesEntry.TABLE_NAME + " (" +
                MovieContract.FavoritesEntry._ID + "INTEGER PRIMARY KEY,"+
                MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY + " INTEGER UNIQUE NOT NULL," +
                MovieContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY + ") REFERENCES "+
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MovieContract.ReviewsEntry.TABLE_NAME + " (" +
                MovieContract.ReviewsEntry._ID+ " INTEGER PRIMARY KEY," +
                MovieContract.ReviewsEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL, " +
                MovieContract.ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewsEntry.COLUMN_URL + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY + ") REFERENCES "+
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "), "+
                "UNIQUE ("+ MovieContract.ReviewsEntry.COLUMN_REVIEW_ID+", "+
                MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY+") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MovieContract.TrailersEntry.TABLE_NAME + " (" +
                MovieContract.TrailersEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.TrailersEntry.COLUMN_TRAILER_ID+ " TEXT NOT NULL," +
                MovieContract.TrailersEntry.COLUMN_MOVIE_KEY+ " TEXT NOT NULL," +
                MovieContract.TrailersEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MovieContract.TrailersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MovieContract.TrailersEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + MovieContract.TrailersEntry.COLUMN_MOVIE_KEY + ") REFERENCES "+
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "), "+
                "UNIQUE ("+ MovieContract.TrailersEntry.COLUMN_TRAILER_ID+", "+
                MovieContract.TrailersEntry.COLUMN_MOVIE_KEY+") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);

        }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoritesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailersEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}


