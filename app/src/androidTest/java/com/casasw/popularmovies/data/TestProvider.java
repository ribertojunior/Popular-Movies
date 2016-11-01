/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casasw.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.casasw.popularmovies.data.MovieContract.FavoritesEntry;
import com.casasw.popularmovies.data.MovieContract.MovieEntry;

import static com.casasw.popularmovies.data.TestUtilities.createMoviesValues;


/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.

    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                FavoritesEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.delete(FavoritesEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the MovieProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MovieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        long testMovieId = 1001;

        type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieUri(testMovieId));
        assertEquals("Error: the MovieEntr.buildMovieUri should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);        
        
        type = mContext.getContentResolver().getType(FavoritesEntry.CONTENT_URI);        
        assertEquals("Error: the FavoriteEntry.CONTENT_URI should return FavoriteEntry.CONTENT_TYPE",
                FavoritesEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(
                FavoritesEntry.buildFavoritesUri(testMovieId));        
        assertEquals("Error: the FavoritesEntry.buildFavoritesUri should return FavoritesEntry.CONTENT_TYPE",
                FavoritesEntry.CONTENT_TYPE, type);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic movie query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicMovieQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues moviesValues = createMoviesValues();
        ContentValues testValues = TestUtilities.createFavoriteValues(moviesValues.getAsLong(MovieEntry._ID));
        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, moviesValues);
        long movieID = TestUtilities.insertFavoriteValues(mContext);
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);
       /* Cursor movieTable  = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);
       Cursor favoritesTable = db.query(FavoritesEntry.TABLE_NAME, null, null, null, null, null, null);
        if (movieTable.moveToFirst())
            Log.v(LOG_TAG, "testBasicMovieQuery:  Cursor movie: "
                    + movieTable.getString(0)+" "+ movieTable.getString(1)+" "
                    + movieTable.getString(2)+" "+ movieTable.getString(3)+" "
                    + movieTable.getString(4)+" "+ movieTable.getString(5)+" "
                    + movieTable.getString(6)+" "+ movieTable.getString(7)+" ");


        if (favoritesTable.moveToFirst())
            Log.v(LOG_TAG, "testBasicMovieQuery:  Cursor favorites: "
                    + favoritesTable.getString(0)+" "+ favoritesTable.getString(1)+" ");*/
        
        db.close();

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, moviesValues);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */
    public void testBasicQueries() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = TestUtilities.createMoviesValues();
        long row = db.insert(MovieEntry.TABLE_NAME, null, values);

        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.FavoritesEntry.COLUMN_MOVIE_ID,
                (Long)values.get(MovieContract.MovieEntry._ID));
        testValues.put(MovieContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE,
                (String) values.get(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
        long rowFav = db.insert(FavoritesEntry.TABLE_NAME, null, testValues);

        //long favoritesId = TestUtilities.insertFavoriteValues(mContext);

        // Test the basic content provider query
        Cursor favoritesCursor = mContext.getContentResolver().query(
                FavoritesEntry.CONTENT_URI,
                null,
                null,
                new String[]{"popular"},
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicQueries, favorites query", favoritesCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    favoritesCursor.getNotificationUri(), FavoritesEntry.CONTENT_URI);
        }
    }

    
    public void testUpdateMovies() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMoviesValues();

        Uri uri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, values);
        long id = ContentUris.parseId(uri);
        // Verify we got a row back.
        assertTrue(id != -1);
        Log.d(LOG_TAG, "New row id: " + id);


        ContentValues updatedValues = new ContentValues(values);        
        updatedValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Meuzovo. De novo Update!");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues, MovieEntry._ID+ " = ?",
                new String[] { Long.toString(values.getAsLong(MovieEntry._ID))});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,   // projection
                MovieEntry._ID + " = " + values.getAsLong(MovieEntry._ID),
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateMovie.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {

        ContentValues movieValues = TestUtilities.createMoviesValues();
        // The TestContentObserver is a one-shot class
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        Uri movieInsertUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, movieValues);
        assertTrue(movieInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert movie
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry insert.",
                movieCursor, movieValues);

        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.FavoritesEntry.COLUMN_MOVIE_ID,(Long) movieValues.get(MovieContract.MovieEntry._ID));
        testValues.put(MovieContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE,(String) movieValues.get(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));

        // Register a content observer for our insert.  This time, directly with the content resolver
        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FavoritesEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(FavoritesEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long id = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(id != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                FavoritesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                new String[]{"popular"}, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating FavoritesEntry.",
                cursor, testValues);
    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver locationObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(FavoritesEntry.CONTENT_URI, true, locationObserver);

        // Register a content observer for our movie delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        locationObserver.waitForNotificationOrFail();
        movieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(locationObserver);
        mContext.getContentResolver().unregisterContentObserver(movieObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertMovieValues(long movieId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry._ID, movieId+i);
            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "/fsdfsfds"+i);
            movieValues.put(MovieEntry.COLUMN_OVERVIEW, "Puta filme saco!");
            movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Meuzovo. De novo!");
            movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "/sfasfasfsa"+i);
            movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, (75 + i)/100);
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "06/09/"+(1969+i));
            movieValues.put(MovieEntry.COLUMN_MOVIE_LIST, "popular");
            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }


    public void testBulkInsert() {

        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues(2001);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        /*// we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);*/

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.moveToFirst();
        ContentValues testValues = new ContentValues();
        for (int i=0;i<cursor.getColumnCount(); i++) {
            if (cursor.getColumnName(i).toString().equals("movie_id")) {
                testValues.put(cursor.getColumnName(i), (cursor.getLong(i)));
            } else {
                testValues.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        Uri uri = mContext.getContentResolver().insert(FavoritesEntry.CONTENT_URI, testValues);
        long id = ContentUris.parseId(uri);

        // Verify we got a row back.
        assertTrue(id != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.
        /*MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor movieTable  = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        Cursor favoritesTable = db.query(FavoritesEntry.TABLE_NAME, null, null, null, null, null, null);
        db.close();*/

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                FavoritesEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                new String[]{"popular"}, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating FavoritesEntry.",
                cursor, testValues);

        cursor.close();
    }
}
