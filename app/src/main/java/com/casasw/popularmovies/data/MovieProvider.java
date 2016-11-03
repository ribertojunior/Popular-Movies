package com.casasw.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.casasw.popularmovies.Utilities;


public class MovieProvider extends ContentProvider {

    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_ID = 101;
    static final int FAVORITES = 102;
    static final int FAVORITES_ID = 103;
    static final int REVIEWS = 104;
    static final int REVIEWS_ID = 105;
    static final int TRAILERS = 106;
    static final int TRAILERS_ID = 107;
    static final int MOVIE_REVIEWS_TRAILER_ID = 108;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

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


    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME
        );
    }

    private static final SQLiteQueryBuilder sMovieListQueryBuilder;
    static {
        sMovieListQueryBuilder = new SQLiteQueryBuilder();
        sMovieListQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavoritesEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.FavoritesEntry.TABLE_NAME +
                        "." + MovieContract.FavoritesEntry.COLUMN_MOVIE_ID
        );
    }

    private static final SQLiteQueryBuilder sMovieTrailerQueryBuilder;
    static {
        sMovieTrailerQueryBuilder = new SQLiteQueryBuilder();
        sMovieTrailerQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailersEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "."  + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.TrailersEntry.TABLE_NAME +
                        "."  + MovieContract.TrailersEntry.COLUMN_MOVIE_KEY
        );
    }
    private static final SQLiteQueryBuilder sMovieReviewsQueryBuilder;
    static {
        sMovieReviewsQueryBuilder = new SQLiteQueryBuilder();
        sMovieReviewsQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewsEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.ReviewsEntry.TABLE_NAME +
                        "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY
        );
    }
    /*New query builders*/
    private static final SQLiteQueryBuilder sMovieReviewsTrailerQueryBuilder;
    static {
        sMovieReviewsTrailerQueryBuilder = new SQLiteQueryBuilder();
        sMovieReviewsTrailerQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailersEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "."  + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.TrailersEntry.TABLE_NAME +
                        "."  + MovieContract.TrailersEntry.COLUMN_MOVIE_KEY +
                        " INNER JOIN " +
                        MovieContract.ReviewsEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.ReviewsEntry.TABLE_NAME +
                        "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY
        );
    }


    /*End of new query builders*/

    private static final String sMovieFavoritesSelection =
            MovieContract.FavoritesEntry.TABLE_NAME + ";";

    private static final String sMovieListSelection =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_LIST + " = ?";

    private static final String sMovieIDSelection =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
    private String LOG_TAG = MovieProvider.class.getSimpleName();

    private Cursor getFavoritesList(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs = null;
        String selection = sMovieFavoritesSelection;

        return sMovieListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getFavoritesMovies(Uri uri, String[] projection,String[] args,  String sortOrder) {

        return sMovieListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieListSelection,
                args,
                null,
                null,
                sortOrder
        );

    }

    private Cursor getMovieReviewsTrailers (Uri uri, String[] projection, String sortOrder) {
        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getMovieIDFromUri(uri)};
        SQLiteQueryBuilder[] queryBuilders = {sMovieReviewsTrailerQueryBuilder, sMovieTrailerQueryBuilder, sMovieReviewsQueryBuilder, sMovieQueryBuilder};
        String[][] projections = {projection, MOVIE_TRAILERS_COLUMNS, MOVIE_REVIEWS_COLUMNS, null};
        Cursor retCursor;
        int i =0;
        do {
            retCursor = queryBuilders[i].query(mOpenHelper.getReadableDatabase(),
                    projections[i],
                    sMovieIDSelection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
            i++;
        }while (!retCursor.moveToFirst());

        return retCursor;
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES+"/#", MOVIE_ID);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES+"/"+
                        MovieContract.PATH_REVIEWS+"/"+
                        MovieContract.PATH_TRAILERS+"/#", MOVIE_REVIEWS_TRAILER_ID);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES+"/#", FAVORITES_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEWS+"/#", REVIEWS_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILERS+"/#", TRAILERS_ID);

        // 3) Return the new matcher!
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS_TRAILER_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            case FAVORITES_ID:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            case REVIEWS:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            case REVIEWS_ID:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            case TRAILERS:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            case TRAILERS_ID:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                if (selectionArgs == null) {
                    selectionArgs = new String[] {Utilities.getMoviesList(getContext())};
                }
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        sMovieListSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_ID: {
                selectionArgs = new String[]{MovieContract.MovieEntry.getMovieIDFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        sMovieIDSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_REVIEWS_TRAILER_ID: {
                retCursor = getMovieReviewsTrailers(uri,projection, sortOrder);
                break;
            }
            case FAVORITES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRAILERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);


        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES: {
                long _id = db.insert(MovieContract.FavoritesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = MovieContract.FavoritesEntry.buildFavoritesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewsEntry.buildReviewsUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;                
            }
            case TRAILERS: {
                long _id = db.insert(MovieContract.TrailersEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = MovieContract.TrailersEntry.buildTrailersUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int del;
        if (s == null) {s="1";} //deleting every registry

        switch (match){
            case MOVIE: {
                del = db.delete(MovieContract.MovieEntry.TABLE_NAME, s, strings);
                break;
            }
            case FAVORITES: {
                del = db.delete(MovieContract.FavoritesEntry.TABLE_NAME, s, strings);
                break;
            }
            case REVIEWS: {
                del = db.delete(MovieContract.ReviewsEntry.TABLE_NAME,s,strings);
                break;
            }
            case TRAILERS: {
                del = db.delete(MovieContract.TrailersEntry.TABLE_NAME,s,strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (del != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return del;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int ups;
        switch (match){
            case MOVIE: {
                ups = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, s, strings);
                break;
            }
            case FAVORITES: {
                ups = db.update(MovieContract.FavoritesEntry.TABLE_NAME, contentValues, s, strings);
                break;
            }
            case REVIEWS: {
                ups = db.update(MovieContract.ReviewsEntry.TABLE_NAME, contentValues, s, strings);
                break;
            }
            case TRAILERS: {
                ups = db.update(MovieContract.TrailersEntry.TABLE_NAME, contentValues, s, strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (ups != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ups;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //Log.v(LOG_TAG, "bulkInsert: Database size: "+getContext().getDatabasePath(mOpenHelper.getDatabaseName()).length()+" bytes.");

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                db.beginTransaction();
                int count = 0;
                try {
                    for (ContentValues value : values){
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id !=-1)
                            count++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            default:
                return super.bulkInsert(uri, values);

        }

    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
