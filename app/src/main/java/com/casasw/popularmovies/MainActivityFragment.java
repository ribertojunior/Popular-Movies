package com.casasw.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.HashMap;
import java.util.Iterator;

import br.com.kots.mob.complex.preferences.ComplexPreferences;

import static com.casasw.popularmovies.Utilities.uriMaker;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    ImageAdapter imageAdapter;
    GridView gridView;
    ArrayList<Movie> moviesList;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;


    public MainActivityFragment() {
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String orderBy = sharedPreferences.getString(getString(R.string.pref_order_key),
                getString(R.string.pref_order_default));

        //Log.v(LOG_TAG, "order by: "+ orderBy);
        fetchMoviesTask.execute(orderBy);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                        if (s.equals(getString(R.string.pref_order_key))) {
                            updateMovies();
                        }
                    }
                };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
        imageAdapter = new ImageAdapter(getContext(), new ArrayList<Uri>());
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(),DetailActivity.class);
                /**Bundle extras = new Bundle();
                extras.putString("EXTRA_OVERVIEW", moviesList.get(i).getOverview());
                extras.putString("EXTRA_TITLE",moviesList.get(i).getTitle());
                extras.putString("EXTRA_THUMBNAIL",moviesList.get(i).getThumbNail());
                extras.putString("EXTRA_RATING",moviesList.get(i).getVoteAvg());
                extras.putString("EXTRA_RELEASE",moviesList.get(i).getReleaseDate());*/
                Movie movie = new Movie(moviesList.get(i).getId(),moviesList.get(i).getTitle(),
                        moviesList.get(i).getPoster(), moviesList.get(i).getThumbNail(),
                        moviesList.get(i).getOverview(),moviesList.get(i).getVoteAvg(),
                        moviesList.get(i).getReleaseDate());
                intent.putExtra("EXTRA_MOVIE", movie);
                intent.putExtra("EXTRA_POSITION",i);
                //intent.putExtras(extras);
                startActivity(intent);


            }
        });

        //gridView.smoothScrollToPosition(index);
        updateMovies();

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[][]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();



        @Override
        protected String[][] doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }
            if (!isOnline()){
                String[][] ret = {{"offline"}};
                return ret;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;
            String movieList = strings[0];
            //Log.v(LOG_TAG, "Order by: " + movieList);

            if (movieList.equals("favorites")){
                final String MPREF = "mPref";
                final String FMOVIES = "fMovies";
                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(),MPREF, Context.MODE_PRIVATE);

                ListMovies listMovies = complexPreferences.getObject(FMOVIES, ListMovies.class);
                if (listMovies == null || listMovies.getMovies() == null) {
                    Log.v(LOG_TAG, "doInBackground : complexPreferences is returning null.");
                    listMovies = new ListMovies(new HashMap<Integer, Movie>());
                }
                HashMap<Integer, Movie> hashMovies = listMovies.getMovies();
                String[][] resultStr = new String[hashMovies.size()][7];
                Iterator<Movie> it = hashMovies.values().iterator();
                Movie m;
                int i =0;
                while (it.hasNext()) {
                    m = it.next();
                    resultStr[i][0] = "/"+m.getPoster();
                    resultStr[i][1] = m.getOverview();
                    resultStr[i][2] = m.getTitle();

                    resultStr[i][3] = "/"+m.getThumbNail();
                    resultStr[i][4] = m.getVoteAvg();
                    resultStr[i][5] = m.getReleaseDate();
                    resultStr[i][6] = ""+m.getId();

                    i++;
                }

                return resultStr;
            }

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
                return getMoviesDataFromJson(moviesJsonStr);

            }catch (JSONException e ){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private String[][] getMoviesDataFromJson(String moviesJsonStr)  throws JSONException{

            final String[] PARAMS = {"results","poster_path","overview",
                    "original_title","backdrop_path","vote_average",
                    "release_date", "id"};

            /**final String RESULTS = "results";
             final String POSTER = "poster_path";
             final String OVERVIEW = "overview";
             final String TITLE = "title";
             final String THUMBNAIL = "backdrop_path";
             final String USER_RATING = "vote_average";
             final String RELEASE_DATE = "release_date";*/

            //Log.v(LOG_TAG, "Original JSON: \n" + moviesJsonStr.toString());

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultJsonArray = moviesJson.getJSONArray(PARAMS[0]);

            //Log.v(LOG_TAG, "Result JSONArray: \n"+resultJsonArray.toString());

            String[][] resultStr = new String[resultJsonArray.length()][PARAMS.length - 1];
            JSONObject movieList;
            for (int i = 0; i< resultJsonArray.length(); i++) {

            /*String poster; String overview; String title;
            String thumbnail; String rating; String releaseDate;*/

                movieList = resultJsonArray.getJSONObject(i);

            /*poster = movieList.getString(POSTER); overview = movieList.getString(OVERVIEW);
            title = movieList.getString(TITLE); thumbnail = movieList.getString(THUMBNAIL);
            rating = movieList.getString(USER_RATING); releaseDate = movieList.getString(RELEASE_DATE);*/

                //Log.v(LOG_TAG, "Movie ["+i+"]");

                for (int j=0;j<PARAMS.length-1;j++) {
                    resultStr[i][j]=movieList.getString(PARAMS[j+1]);
                    //Log.v(LOG_TAG, PARAMS[j+1] +" - "+ resultStr[i][j] +" - "+ j);
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

        @Override
        protected void onPostExecute(String[][] strings) {
            //Log.v(LOG_TAG,"string[1][1]" + strings[0][0].substring(1));
            if (strings != null) {
                if (!strings[0][0].equals("offline")) {
                    moviesList = new ArrayList<Movie>();
                    ArrayList<Uri> uriList = new ArrayList<Uri>();
                    //Uri.Builder builder = new Uri.Builder();

                    for (int i = 0; i < strings.length; i++) {
                    /*builder.scheme("http")
                            .authority("image.tmdb.org")
                            .appendPath("t")
                            .appendPath("p")
                            .appendPath("w185")
                            .appendPath("")
                            .appendPath(strings[i][0].substring(1));*/

                        uriList.add(uriMaker(strings[i][0].substring(1)));
                    /* posterPath - overview - title - thumbnail - rating - date - id
                    ((Integer id, String title, String poster,  String thumbNail, String overview,
                      String voteAvg, String releaseDate))*/
                        moviesList.add(new Movie(Integer.parseInt(strings[i][6]), strings[i][2], strings[i][0].substring(1),
                                strings[i][3].substring(1), strings[i][1], strings[i][4], strings[i][5]));
                    /*Log.v(LOG_TAG, strings[i][0]+" - "+strings[i][1]+" - "+strings[i][2]+" - "+
                            strings[i][3]+" - "+strings[i][4]+" - "+strings[i][5]+" - "+strings[i][6]);*/
                        //Log.v(LOG_TAG, builder.build().toString() + " string["+i+"][3]" + strings[i][3].substring(1));
                        //builder = new Uri.Builder();

                    }


                    imageAdapter = new ImageAdapter(getContext(), uriList);
                    gridView.setAdapter(imageAdapter);
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.grid_view_posters), R.string.error_internet,
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            }else {
                Log.e(LOG_TAG, "onPostExecute - Return string is null!");
            }
        }

        private boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //updateMovies();
    }



}