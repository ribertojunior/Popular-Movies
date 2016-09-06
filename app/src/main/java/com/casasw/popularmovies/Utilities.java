package com.casasw.popularmovies;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Junior on 31/08/2016.
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

         //Log.v(LOG_TAG, "Original JSON: \n" + jsonStr.toString());

        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray resultJsonArray = jsonObject.getJSONArray(params[0]);

        //Log.v(LOG_TAG, "Result JSONArray: \n"+resultJsonArray.toString());

        String[][] resultStr = new String[resultJsonArray.length()][params.length - 1];
        JSONObject jsonList;
        for (int i = 0; i< resultJsonArray.length(); i++) {

            jsonList = resultJsonArray.getJSONObject(i);
            //Log.v(LOG_TAG, "Movie ["+i+"]");

            for (int j=0;j<params.length-1;j++) {
                resultStr[i][j]=jsonList.getString(params[j+1]);
                //Log.v(LOG_TAG, params[j+1] +" - "+ resultStr[i][j] +" - "+ j);
            }
        }
            /*for (String[] row : resultStr
                 ) {
                for (String item : row
                     ) {
                    Log.v(LOG_TAG, item);
                }

            }
            Log.v(LOG_TAG, "Total :"+resultStr.toString());*/
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

}
