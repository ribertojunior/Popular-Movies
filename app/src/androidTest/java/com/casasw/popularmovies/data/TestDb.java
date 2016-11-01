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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.FavoritesEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewsEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailersEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_LIST);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSITION);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);

        } while(c.moveToNext());


        assertTrue("Error: The database doesn't contain all of the required movie entry columns.",
                movieColumnHashSet.isEmpty());
        db.close();
    }



    public void testMovieTable() {


        // First step: Get reference to writable database
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        // Create ContentValues of what you want to insert
        // (you can use the createMoviesValues TestUtilities function if you wish)
        ContentValues cv = com.casasw.popularmovies.data.TestUtilities.createMoviesValues();
        // Insert ContentValues into database and get a row ID back
        long in = db.insert(MovieContract.MovieEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in movie table has fail.", in != -1 );
        // Query the database and receive a Cursor back
        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        // Move the cursor to a valid database row
        assertTrue("Error: Weather table select has fail.",
                c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        com.casasw.popularmovies.data.TestUtilities.validateCurrentRecord("Error: The returning cursor is not equals to ContentValues inserted.", c, cv);
        //testing the unique clause
        in = db.insert(MovieContract.MovieEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in movie table has fail.", in != -1 );
        // Query the database and receive a Cursor back
        c = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        // Move the cursor to a valid database row
        assertTrue("Error: Weather table select has fail.",
                c.moveToFirst());
        assertTrue("Error: Unique clause is not working properly. We got ["+c.getCount()+"] rows.", c.getCount()==1);
        // Finally, close the cursor and database
        c.close();
        db.close();
    }

    public void testReviewTable(){
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_ID, "10012");
        cv.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY, "1001");
        cv.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, "Meuzovo da Silva Sauro");
        cv.put(MovieContract.ReviewsEntry.COLUMN_URL, "www.example.com/1001");

        // Insert ContentValues into database and get a row ID back
        long in = db.insert(MovieContract.ReviewsEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in table has fail.", in != -1 );
        // Query the database and receive a Cursor back
        Cursor c = db.query(MovieContract.ReviewsEntry.TABLE_NAME, null, null, null, null, null, null);
        // Move the cursor to a valid database row
        assertTrue("Error: Weather table select has fail.",
                c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        com.casasw.popularmovies.data.TestUtilities.validateCurrentRecord("Error: The returning cursor is not equals to ContentValues inserted.", c, cv);
        //testing conflict
        in = db.insert(MovieContract.ReviewsEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in table has fail.", in != -1 );
        c = db.query(MovieContract.ReviewsEntry.TABLE_NAME, null, null, null, null, null, null);
        // Move the cursor to a valid database row
        assertTrue("Error: Weather table select has fail.",
                c.moveToFirst());

        assertEquals("Error: unique clause is not working properly. We have ["+c.getCount()+"] rows", 1, c.getCount());
        // Finally, close the cursor and database
        c.close();
        db.close();
    }

    public void testInnerJoin() {
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        ContentValues cv = TestUtilities.createMoviesValues();
        ContentValues cvFav = cv;
        long[] movieId = new long[2];
        movieId[0] = cv.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        long in = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
        assertTrue("Error: Insertion in movie table has fail.", in != -1 );

        cvFav.remove(MovieContract.MovieEntry.COLUMN_POSITION);
        cvFav.remove(MovieContract.MovieEntry.COLUMN_MOVIE_LIST);
        in = db.insert(MovieContract.FavoritesEntry.TABLE_NAME, null, cvFav);
        assertTrue("Error: Insertion in FavoritesEntry table has fail.", in != -1 );


        //Another movie
        cv = TestUtilities.createMoviesValues();
        cv.put(MovieContract.MovieEntry.COLUMN_POSITION, 2);
        in = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
        assertTrue("Error: 2nd insertion in movie table has fail.", in != -1 );
        movieId[1] = cv.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        for (int i=0;i<2;i++) {
            for (int j = 0; j<3;j++) {
                cv = TestUtilities.createReviewsValues(movieId[i]);

                in = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, cv);
                assertTrue("Error: Insertion in ReviewsEntry table has fail.", in != -1 );

                cv = TestUtilities.createTrailersValues(movieId[i]);

                in = db.insert(MovieContract.TrailersEntry.TABLE_NAME, null, cv);
                assertTrue("Error: Insertion in TrailersEntry table has fail.", in != -1 );
            }
        }


        SQLiteQueryBuilder sMovieQueryBuilder;
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewsEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "."  + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.ReviewsEntry.TABLE_NAME +
                        "."  + MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY

        );
        /*String selection = MovieContract.MovieEntry.TABLE_NAME +
                "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? AND " +
                MovieContract.ReviewsEntry.TABLE_NAME +
                "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = new String[]{""+movieId, ""+movieId};*/
        String selection = MovieContract.MovieEntry.TABLE_NAME +
                "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{""+movieId[0]};

        Cursor c = sMovieQueryBuilder.query(db, TestUtilities.MOVIE_REVIEWS_COLUMNS, selection, selectionArgs, null, null, null);
        assertTrue("Error REVIEWS inner join returning zero rows. ", c.moveToFirst());
        Log.v(LOG_TAG, "testInnerJoin: Reviews");

        String col = "";
        for (int i =0;i<c.getColumnCount();i++) {
            col =  col + "["+c.getColumnName(i)+"] - ";
        }
        String values = "";
        do {
            for (int i =0;i<c.getColumnCount();i++) {
                values =  values + c.getString(i) +" - ";
            }
            values = values + "\n";

        }while (c.moveToNext());
        Log.v(LOG_TAG, col);
        Log.v(LOG_TAG, values);

        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailersEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "."  + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.TrailersEntry.TABLE_NAME +
                        "."  + MovieContract.TrailersEntry.COLUMN_MOVIE_KEY
        );




        c = sMovieQueryBuilder.query(db, TestUtilities.MOVIE_TRAILERS_COLUMNS, selection, selectionArgs, null, null, null);
        assertTrue("Error trailers inner join returning zero rows. ", c.moveToFirst());
        Log.v(LOG_TAG, "testInnerJoin: Trailers");

        col = "";
        for (int i =0;i<c.getColumnCount();i++) {
            col =  col + "["+c.getColumnName(i)+"] - ";
        }
        values = "";
        do {
            for (int i =0;i<c.getColumnCount();i++) {
                values =  values + c.getString(i) +" - ";
            }
            values = values + "\n";

        }while (c.moveToNext());
        Log.v(LOG_TAG, col);
        Log.v(LOG_TAG, values);


        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(
                MovieContract.FavoritesEntry.TABLE_NAME+ " INNER JOIN " +
                        MovieContract.ReviewsEntry.TABLE_NAME +
                        " ON " + MovieContract.FavoritesEntry.TABLE_NAME +
                        "."  + MovieContract.FavoritesEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.ReviewsEntry.TABLE_NAME +
                        "."  + MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY +
                        " INNER JOIN " +
                        MovieContract.TrailersEntry.TABLE_NAME +
                        " ON " + MovieContract.FavoritesEntry.TABLE_NAME +
                        "."  + MovieContract.FavoritesEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.FavoritesEntry.TABLE_NAME +
                        "."  + MovieContract.FavoritesEntry.COLUMN_MOVIE_ID

        );


        c = sMovieQueryBuilder.query(db, TestUtilities.MOVIE_DETAIL_COLUMNS, null, null, null, null, null);
        assertTrue("Error detail inner join returning zero rows. ", c.moveToFirst());

        Log.v(LOG_TAG, "testInnerJoin: Complete with favorites");

        col = "";
        for (int i =0;i<c.getColumnCount();i++) {
            col =  col + "["+c.getColumnName(i)+"] - ";
        }
        values = "";
        do {
            for (int i =0;i<c.getColumnCount();i++) {
                values =  values + c.getString(i) +" - ";
            }
            values = values + "\n";

        }while (c.moveToNext());
        Log.v(LOG_TAG, col);
        Log.v(LOG_TAG, values);

    }

    public void testTrailerTable(){
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.TrailersEntry.COLUMN_TRAILER_ID, "10012");
        cv.put(MovieContract.TrailersEntry.COLUMN_MOVIE_KEY, "1001");
        cv.put(MovieContract.TrailersEntry.COLUMN_KEY, "vfgsd5");
        cv.put(MovieContract.TrailersEntry.COLUMN_NAME, "Meuzovo");
        cv.put(MovieContract.TrailersEntry.COLUMN_SITE, "vimeo");

        // Insert ContentValues into database and get a row ID back
        long in = db.insert(MovieContract.TrailersEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in movie table has fail.", in != -1 );
        // Query the database and receive a Cursor back
        Cursor c = db.query(MovieContract.TrailersEntry.TABLE_NAME, null, null, null, null, null, null);
        // Move the cursor to a valid database row
        assertTrue("Error: Weather table select has fail.",
                c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        com.casasw.popularmovies.data.TestUtilities.validateCurrentRecord("Error: The returning cursor is not equals to ContentValues inserted.", c, cv);
        //testing the unique clause
        in = db.insert(MovieContract.TrailersEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in movie table has fail.", in != -1 );
        // Query the database and receive a Cursor back
        c = db.query(MovieContract.TrailersEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: Unique clause is not working properly. We got ["+c.getCount()+"] rows", c.getCount()==1);
        // Finally, close the cursor and database
        c.close();
        db.close();
    }


    /*
        Students: This is a helper method for the testMovieTable quiz. You can move your
        code from testFavoritesTable to here so that you can call this code from both
        testMovieTable and testFavoritesTable.
     */
    public void testFavoritesTable() {
        String testMovieKey = "010101";
        String testOriginalTitle = "Meuzovo. De novo!";
        // First step: Get reference to writable database
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        // Create ContentValues of what you want to insert
        // (you can use the createFavoriteValues if you wish)
        //ContentValues cv = TestUtilities.createFavoriteValues();
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.FavoritesEntry.COLUMN_MOVIE_ID, testMovieKey);
        cv.put(MovieContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE, testOriginalTitle);


        // Insert ContentValues into database and get a row ID back
        long in = db.insert(MovieContract.FavoritesEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Unique clause is not working properly.", in != -1 );
        // Query the database and receive a Cursor back
        //Cursor c = db.rawQuery("SELECT * FROM " + WeatherContract.LocationEntry.TABLE_NAME, null);
        Cursor c = db.query(MovieContract.FavoritesEntry.TABLE_NAME, null, null, null, null, null, null);
        //assertTrue("rawquery doesn't return the same than query.", c.equals(c2));
        // Move the cursor to a valid database row
        assertTrue("Error: Location table select has fail.",
                c.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        com.casasw.popularmovies.data.TestUtilities.validateCurrentRecord("Error: The returning cursor is not equals to ContentValues inserted.", c, cv);

        //testing unique clause
        in = db.insert(MovieContract.FavoritesEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in location table has fail.", in == -1 );


        assertFalse( "Error: More than one record returned from location query",
                c.moveToNext() );

        // Finally, close the cursor and database
        c.close();
        db.close();

    }


}
