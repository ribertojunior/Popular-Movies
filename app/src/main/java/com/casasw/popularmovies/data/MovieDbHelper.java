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
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_LIST + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_DATABASE_MOVIE_TABLE);

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MovieContract.FavoritesEntry.TABLE_NAME + " (" +
                MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +
                MovieContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY + ") REFERENCES "+
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

        }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}


