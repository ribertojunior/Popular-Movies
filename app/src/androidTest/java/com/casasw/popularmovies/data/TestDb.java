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
import android.test.AndroidTestCase;

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
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
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

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);

        } while(c.moveToNext());


        assertTrue("Error: The database doesn't contain all of the required movie entry columns.",
                movieColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createFavoriteValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testFavoritesTable() {
        long in = insertFavorite();
        assertTrue("Error: Insertion in location table has fail.", in != -1 );

    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createMoviesValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testMovieTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.
        //long locationRowId = insertFavorite();
        // Instead of rewriting all of the code we've already written in testFavoritesTable
        // we can move this code to insertFavorite and then call insertFavorite from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testFavoritesTable can only return void because it's a test.

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
        // Finally, close the cursor and database
        c.close();
        db.close();
    }

    public void testReviewTable(){
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY, "1001");
        cv.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, "Meuzovo da Silva Sauro");
        cv.put(MovieContract.ReviewsEntry.COLUMN_URL, "www.example.com/1001");

        // Insert ContentValues into database and get a row ID back
        long in = db.insert(MovieContract.ReviewsEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in movie table has fail.", in != -1 );
        // Query the database and receive a Cursor back
        Cursor c = db.query(MovieContract.ReviewsEntry.TABLE_NAME, null, null, null, null, null, null);
        // Move the cursor to a valid database row
        assertTrue("Error: Weather table select has fail.",
                c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        com.casasw.popularmovies.data.TestUtilities.validateCurrentRecord("Error: The returning cursor is not equals to ContentValues inserted.", c, cv);
        // Finally, close the cursor and database
        c.close();
        db.close();
    }

    public void testTrailerTable(){
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();

        ContentValues cv = new ContentValues();
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
        // Finally, close the cursor and database
        c.close();
        db.close();
    }


    /*
        Students: This is a helper method for the testMovieTable quiz. You can move your
        code from testFavoritesTable to here so that you can call this code from both
        testMovieTable and testFavoritesTable.
     */
    public long insertFavorite() {
        String testMovieKey = "010101";
        String testOriginalTitle = "Meuzovo. De novo!";
        // First step: Get reference to writable database
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        // Create ContentValues of what you want to insert
        // (you can use the createFavoriteValues if you wish)
        //ContentValues cv = TestUtilities.createFavoriteValues();
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY, testMovieKey);
        cv.put(MovieContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE, testOriginalTitle);


        // Insert ContentValues into database and get a row ID back
        long in = db.insert(MovieContract.FavoritesEntry.TABLE_NAME,null, cv);
        assertTrue("Error: Insertion in location table has fail.", in != -1 );
        // Query the database and receive a Cursor back
        //Cursor c = db.rawQuery("SELECT * FROM " + WeatherContract.LocationEntry.TABLE_NAME, null);
        Cursor c = db.query(MovieContract.FavoritesEntry.TABLE_NAME, null, null, null, null, null, null);
        //assertTrue("rawquery doesn't return the same than query.", c.equals(c2));
        // Move the cursor to a valid database row
        assertTrue("Error: Location table select has fail.",
                c.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        com.casasw.popularmovies.data.TestUtilities.validateCurrentRecord("Error: The returning cursor is not equals to ContentValues inserted.", c, cv);
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        assertFalse( "Error: More than one record returned from location query",
                c.moveToNext() );

        // Finally, close the cursor and database
        c.close();
        db.close();

        return in;
    }


}
