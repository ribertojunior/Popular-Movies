package com.casasw.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.casasw.popularmovies.Utilities.uriMaker;


public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<String[][]>> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;

    public FetchTrailersTask(Context mContext, LayoutInflater mInflater) {
        this.mContext = mContext;
        this.mInflater = mInflater;
    }

    @Override
    protected ArrayList<String[][]> doInBackground(String... strings) {
        if (strings.length == 0) {
            return null;
        }
        ArrayList<String[][]> result = new ArrayList<String[][]>();
        if (!isOnline()){
            String[][] ret = {{"offline"}};
            result.add(ret);
            return result;
        }
        String[][] retTrailers = null;
        String[][] retReviews = null;
        String movieId = strings[0];
        String jSonStr = null;
        try {

            String path[] = {"3","movie",movieId, "videos"};
            URL url = new URL(uriMaker(
                    "api.themoviedb.org", path, "api_key",
                    BuildConfig.MovieDBApiKey).toString());
            //Log.v(LOG_TAG, url.toString());
            jSonStr = fetchData(url);
            String [] params = {"results", "key", "name", "site"};
            retTrailers = getDataFromJson(jSonStr, params);
            result.add(retTrailers);

            path[3] = "reviews";
            url = new URL(uriMaker(
                    "api.themoviedb.org", path, "api_key",
                    BuildConfig.MovieDBApiKey).toString());
            params[1] = "id"; params[2] = "author"; params[3] = "url";
            jSonStr = fetchData(url);
            retReviews = getDataFromJson(jSonStr, params);
            result.add(retReviews);

        }catch (Exception e){
            Log.e(LOG_TAG, e.toString());
        }
        return result;

    }

    @Override
    protected void onPostExecute(final ArrayList<String[][]> list) {
        if (list != null) {
            if (!list.get(0)[0][0].equals("offline")) {

                View itemView;
                ImageView imageView;
                TextView textView;
                View rootView = mInflater.inflate(R.layout.fragment_detail, null);
                LinearLayout trailerLayout = (LinearLayout) rootView.findViewById(R.id.viewTrailer);
                LinearLayout reviewLayout = (LinearLayout) rootView.findViewById(R.id.viewReview);
                final String[][] strings = list.get(0);
                /**
                 * Trailers
                 * */
                for (int i=0;i< strings.length;i++) {
                    //Log.v(LOG_TAG, "|----------Trailer:["+i+"]----------|");
                    itemView = mInflater.inflate(R.layout.view_items, null);
                    imageView = (ImageView) itemView.findViewById(R.id.imageViewPlay);
                    textView = (TextView) itemView.findViewById(R.id.textViewTrailer);
                    textView.setText(mContext.getString(R.string.play_trailer)+" "+strings[i][1]);
                    //Log.v(LOG_TAG, strings[i][j]);
                    final int pos = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /**Snackbar.make(view, "Opening "+strings[pos][1]+": youtube.com/watch?v="+strings[pos][0],
                             Snackbar.LENGTH_SHORT)
                             .show();
                             String authority, String[] path, String queryKey, String queryValue)*/
                            String[] path = {"watch"};
                            //URL url = new URL(Utilities.uriMaker("www.youtube.com", path, "v",strings[pos][0]+"").toString());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uriMaker("www.youtube.com", path, "v",strings[pos][0]+""));
                            mContext.startActivity(intent);
                            //Log.v(LOG_TAG, url.toString());
                        }
                    });

                    trailerLayout.addView(itemView);

                }
                /**
                 * Reviews
                 * */
                final String[][] stringsReview = list.get(1);
                for (int i=0;i< stringsReview.length;i++) {
                    //Log.v(LOG_TAG, "|----------Trailer:["+i+"]----------|");
                    itemView = mInflater.inflate(R.layout.view_items, null);
                    imageView = (ImageView) itemView.findViewById(R.id.imageViewPlay);
                    imageView.setImageResource(R.drawable.text);
                    textView = (TextView) itemView.findViewById(R.id.textViewTrailer);
                    textView.setText(mContext.getString(R.string.read_pre)+" "+stringsReview[i][1]+ mContext.getString(R.string.read_pos));
                    //Log.v(LOG_TAG, stringsReview[i][j]);
                    final int pos = i;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /**Snackbar.make(view, "Opening "+stringsReview[pos][1]+": youtube.com/watch?v="+stringsReview[pos][0],
                             Snackbar.LENGTH_SHORT)
                             .show();
                             String authority, String[] path, String queryKey, String queryValue)*/
                            String[] path = {"watch"};
                            //URL url = new URL(Utilities.uriMaker("www.youtube.com", path, "v",stringsReview[pos][0]+"").toString());
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringsReview[pos][2]+""));
                            mContext.startActivity(intent);
                            //Log.v(LOG_TAG, url.toString());
                        }
                    });

                    reviewLayout.addView(itemView);

                }

            }
        }
    }

    /**
     * This method works from a JSon structured this way:
     * {
     *      "params[0]":[
     *          {
     *              "params[1]": "data",
     *              "params[2]": "data2",
     *              ...
     *              "params[n]: "datan"
     *           }
     * }
     * the inner block can be repeated as many as needed.
     *
     * @param jSonStr String containing the JSon
     * @param params params to search the JSon
     * @return the JSon in a matrix data
     * @throws JSONException
     */
    private String[][] getDataFromJson(String jSonStr, String[] params) throws JSONException {

        JSONObject jsonObject = new JSONObject(jSonStr);
        JSONArray resultJsonArray = jsonObject.getJSONArray(params[0]);

        //Log.v(LOG_TAG, "Result JSONArray: \n"+resultJsonArray.toString());

        String[][] resultStr = new String[resultJsonArray.length()][params.length - 1];
        JSONObject movieList;
        for (int i = 0; i< resultJsonArray.length(); i++) {

            /*String poster; String overview; String title;
            String thumbnail; String rating; String releaseDate;*/

            movieList = resultJsonArray.getJSONObject(i);

            /*poster = movieList.getString(POSTER); overview = movieList.getString(OVERVIEW);
            title = movieList.getString(TITLE); thumbnail = movieList.getString(THUMBNAIL);
            rating = movieList.getString(USER_RATING); releaseDate = movieList.getString(RELEASE_DATE);*/

            //Log.v(LOG_TAG, "Movie ["+i+"]");

            for (int j=0;j<params.length-1;j++) {
                resultStr[i][j]=movieList.getString(params[j+1]);
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

    @Nullable
    private String fetchData (URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jSonStr = null;

        try {
            //Log.v(LOG_TAG, url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                jSonStr = null;
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
                jSonStr = null;
            }
            jSonStr = buffer.toString();
            return jSonStr;

        }catch (Exception e){
            Log.e(LOG_TAG, e.toString());
            jSonStr = null;

        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }
        return null;

    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}