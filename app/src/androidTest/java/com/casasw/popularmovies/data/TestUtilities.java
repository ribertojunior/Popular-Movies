package com.casasw.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.casasw.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_MOVIE_KEY = "1001";
    static final String TEST_MOVIE_TITLE = "Meuzovo. De novo!";
    static final String[] MOVIE_REVIEWS_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_LIST,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_URL

    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_ORIGINAL_TITLE = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_POSTER_PATH = 4;
    static final int COL_OVERVIEW = 5;
    static final int COL_VOTE_AVARAGE = 6;
    static final int COL_BACKDROP_PATH = 7;
    static final int COL_MOVIE_LIST = 8;
    static final int COL_REVIEW_AUTHOR = 9;
    static final int COL_REVIEW_URL = 10;

    static final String[] MOVIE_TRAILERS_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_LIST,
            MovieContract.TrailersEntry.COLUMN_SITE,
            MovieContract.TrailersEntry.COLUMN_NAME,
            MovieContract.TrailersEntry.COLUMN_KEY,

    };
    static final int COL_TRAILER_SITE = 9;
    static final int COL_TRAILER_NAME = 10;
    static final int COL_TRAILER_KEY = 11;


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            if (columnName.contentEquals("movie_id")) {
                assertEquals("Value '" + entry.getValue().toString() +
                        "' did not match the expected value '" +
                        expectedValue + "'. " + error, Integer.parseInt(expectedValue), Integer.parseInt(valueCursor.getString(idx)));
            } else {
                assertEquals("Value '" + entry.getValue().toString() +
                        "' did not match the expected value '" +
                        expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
            }
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createMoviesValues() {
        ContentValues values = new ContentValues();
        long movieId = 1001;
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/aisfsaifb28y842");
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Filme bosta dozovo.");
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Meuzovo. De novo!");
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, "/auq38tsayqfina8");
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "6.969");
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "6/9/69");
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_LIST, "popular");

        return values;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
    static ContentValues createFavoriteValues(long id) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY, id);
        testValues.put(MovieContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE, TEST_MOVIE_TITLE);

        return testValues;
    }


    static ContentValues createReviewsValues(long id) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        long review_id = (long) Math.floor(Math.random()*1000);
        testValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_ID, review_id);
        testValues.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY, id);
        testValues.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, "Meuzovo ["+review_id+"]");
        testValues.put(MovieContract.ReviewsEntry.COLUMN_URL, "https://meuzovo.com/"+review_id);

        return testValues;
    }

    /*
       Students: You can uncomment this helper function once you have finished creating the
       LocationEntry part of the WeatherContract.
    */
    static ContentValues createTrailersValues(long id) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        long randomAdd = (long) Math.floor(Math.random()*1000);
        testValues.put(MovieContract.TrailersEntry.COLUMN_MOVIE_KEY, id);
        testValues.put(MovieContract.TrailersEntry.COLUMN_KEY, "fk4BbF7B29w"+randomAdd);
        testValues.put(MovieContract.TrailersEntry.COLUMN_NAME, "Meuzovo ["+randomAdd+"] Original Theatrical Trailer");
        testValues.put(MovieContract.TrailersEntry.COLUMN_SITE, "youtube");

        return testValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    static long insertFavoriteValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createFavoriteValues(1001);

        long movieId;
        movieId = db.insert(MovieContract.FavoritesEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", movieId != -1);

        return movieId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
