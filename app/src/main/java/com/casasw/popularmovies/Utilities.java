package com.casasw.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Junior on 31/08/2016.
 * Utility class is self-explanatory
 */
public class Utilities {

    /**
     * This method was designed to read a JSON string with a array with key equals to params[0]
     * @param jsonStr JSON with data
     * @param params params to read the jsonStr, param[0] should be the array key
     * @return String[][] JSON as a array
     * @throws JSONException
     */
    public static String[][] getDataFromJson(String jsonStr, String[] params)  throws JSONException {



        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray resultJsonArray = jsonObject.getJSONArray(params[0]);



        String[][] resultStr = new String[resultJsonArray.length()][params.length - 1];
        JSONObject jsonList;
        for (int i = 0; i< resultJsonArray.length(); i++) {

            jsonList = resultJsonArray.getJSONObject(i);


            for (int j=0;j<params.length-1;j++) {
                resultStr[i][j]=jsonList.getString(params[j+1]);

            }
        }

        return resultStr;
    }
    public static Uri uriMaker(String jpg) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendPath("")
                .appendPath(jpg);

        return builder.build();
    }
    public static Uri uriMaker(String jpg, String size) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath(size)
                .appendPath("")
                .appendPath(jpg);

        return builder.build();
    }

    public static Uri uriMaker(String authority, String[] path, String queryKey, String queryValue) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(authority);
        for (String p : path
                ) {
            builder.appendPath(p);
        }
        builder.appendQueryParameter(queryKey,queryValue);

        return builder.build();
    }

    public static String getMoviesList(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_order_key),
                context.getString(R.string.pref_order_default));
    }

    /**
     * Remove the year from a string containing a date with the format mm/dd/yyyy or dd/mm/yyyy
     * @param date
     * @return A string containing a year with the format yyyy
     */
    public static String removeYear(String date) {
        return date.substring(5);

    }
}
