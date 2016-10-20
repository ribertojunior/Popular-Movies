package com.casasw.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.casasw.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;

    public FetchMoviesTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... strings) {

        if (strings.length == 0) {
            return null;
        }
        if (!isOnline(mContext)){
            Log.e(LOG_TAG, "doInBackground: No internet connectivity!");
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;
        String movieList = strings[0];
        //Log.v(LOG_TAG, "Order by: " + movieList);
        try {

            final String APPID_PARAM = "api_key";

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieList)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MovieDBApiKey);
            URL url = new URL(builder.build().toString());
            //Log.v(LOG_TAG, "URL: "+url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                moviesJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                moviesJsonStr = null;
            }
            moviesJsonStr = buffer.toString();


        }catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            moviesJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMoviesDataFromJson(moviesJsonStr, movieList);

        }catch (JSONException e ){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private void getMoviesDataFromJson(String moviesJsonStr, String movieList)  throws JSONException{

        final String[] PARAMS = {"results","poster_path","overview",
                "original_title","backdrop_path","vote_average",
                "release_date", "id"};

        //Log.v(LOG_TAG, "Original JSON: \n" + moviesJsonStr.toString());

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultJsonArray = moviesJson.getJSONArray(PARAMS[0]);
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(resultJsonArray.length());

        //Log.v(LOG_TAG, "Result JSONArray: \n"+resultJsonArray.toString());

        String[][] resultStr = new String[resultJsonArray.length()][PARAMS.length - 1];
        JSONObject movieListJSON;
        for (int i = 0; i< resultJsonArray.length(); i++) {
            long id;
            String poster_path;
            String overview;
            String original_title;
            String backdrop_path;
            String vote_average;
            String release_date;

            movieListJSON = resultJsonArray.getJSONObject(i);
            id = movieListJSON.getLong(PARAMS[7]);
            poster_path = movieListJSON.getString(PARAMS[1]);
            overview = movieListJSON.getString(PARAMS[2]);;
            original_title = movieListJSON.getString(PARAMS[3]);;
            backdrop_path = movieListJSON.getString(PARAMS[4]);;
            vote_average = movieListJSON.getString(PARAMS[5]);;
            release_date = movieListJSON.getString(PARAMS[6]);;

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry._ID, id);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, original_title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdrop_path);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_LIST, movieList);

            contentValuesVector.add(movieValues);

        }
        int in = 0;
        if (contentValuesVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(cvArray);
            in = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }

    }



    private boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}