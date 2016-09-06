package com.casasw.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import br.com.kots.mob.complex.preferences.ComplexPreferences;

import static com.casasw.popularmovies.Utilities.uriMaker;

/**
 * Fragment for DetailActivity
 */
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private LinearLayout trailerLayout;


    public DetailActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final String MPREF = "mPref";
        final String FMOVIES = "fMovies";



        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        final Movie movie = extras.getParcelable("EXTRA_MOVIE");
        int textSize = 15;



        final ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(),MPREF, Context.MODE_PRIVATE);

        ListMovies listMovies = complexPreferences.getObject(FMOVIES, ListMovies.class);
        if (listMovies == null || listMovies.getMovies() == null) {
            Log.v(LOG_TAG, "complexPreferences is returning null.");
            listMovies = new ListMovies(new HashMap<Integer, Movie>());
        }
        final HashMap<Integer, Movie> hashMovies = listMovies.getMovies();
        //final HashMap<Integer, Movie> hashMovies = new HashMap<Integer, Movie>();



        TextView textView = (TextView) rootView.findViewById(R.id.movieTitleText);
        textView.setText(movie.getTitle());

        textView = (TextView) rootView.findViewById(R.id.overviewText);
        textView.setText(movie.getOverview());

        textView.setTextSize(textSize);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //imageView.setPadding(8,8,8,8);
        Picasso.with(getContext()).load(
                uriMaker(
                        movie.getThumbNail(), "w342")).into(imageView);
        //"w92", "w154", "w185", "w342", "w500", "w780"

        textView = (TextView) rootView.findViewById(R.id.voteAvgText);
        textView.setText(movie.getVoteAvg()+"/10");

        textView = (TextView) rootView.findViewById(R.id.dateText);
        textView.setText(movie.getReleaseDate().substring(0,4));

        imageView = (ImageView) rootView.findViewById(R.id.imageViewStar);
        if (hashMovies.containsKey(movie.getId())){
            imageView.setImageResource(android.R.drawable.btn_star_big_on);
        }
        final ListMovies finalListMovies = listMovies;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = getString(R.string.error_internet);
                ImageView imageView = (ImageView) view;

                if (imageView.getDrawable().getConstantState().equals(
                        getResources().getDrawable(android.R.drawable.btn_star_big_on).getConstantState())){
                    imageView.setImageResource(android.R.drawable.btn_star_big_off);
                    msg = getString(R.string.favorite_off);
                    hashMovies.remove(movie.getId());

                }else {
                    imageView.setImageResource(android.R.drawable.btn_star_big_on);
                    msg = getString(R.string.favorite_on);
                    hashMovies.put(movie.getId(), movie);

                }
                Snackbar.make(view, msg,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Log.v(LOG_TAG, "New favorited movie list.");
                Iterator<Movie> it = hashMovies.values().iterator();
                while(it.hasNext()){
                    Log.v(LOG_TAG, it.next().getTitle());
                }
                finalListMovies.setMovies(hashMovies);
                complexPreferences.putObject(FMOVIES, finalListMovies);
                complexPreferences.commit();
            }

        });

        trailerLayout = (LinearLayout) rootView.findViewById(R.id.viewTrailer);

        FetchTrailersTask task = new FetchTrailersTask(getContext(), inflater);

        task.execute(movie.getId()+"");


        return rootView;
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, String[][]> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
        private Context mContext;
        private LayoutInflater mInflater;

        public FetchTrailersTask(Context mContext, LayoutInflater mInflater) {
            this.mContext = mContext;
            this.mInflater = mInflater;
        }

        @Override
        protected String[][] doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            if (!isOnline()){
                String[][] ret = {{"offline"}};
                return ret;
            }

            String movieId = strings[0];
            String jSonStr = null;
            try {

                String path[] = {"3","movie",movieId, "videos"};
                URL url = new URL(uriMaker(
                        "api.themoviedb.org", path, "api_key",
                        BuildConfig.MovieDBApiKey).toString());
                //Log.v(LOG_TAG, url.toString());
                jSonStr = fetchData(url);


            }catch (Exception e){
                Log.e(LOG_TAG, e.toString());
            }
            try {

                String [] params = {"results", "key", "name", "site"};
                return getDataFromJson(jSonStr, params);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(final String[][] strings) {
            if (strings != null) {
                if (!strings[0][0].equals("offline")) {

                    View itemView;
                    ImageView imageView;
                    TextView textView;
                    for (int i=0;i< strings.length;i++) {
                        //Log.v(LOG_TAG, "|----------Trailer:["+i+"]----------|");
                        itemView = mInflater.inflate(R.layout.view_trailer_items, null);
                        imageView = (ImageView) itemView.findViewById(R.id.imageViewPlay);
                        textView = (TextView) itemView.findViewById(R.id.textViewTrailer);
                        textView.setText(textView.getText()+" "+strings[i][1]);
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
                                Intent intent = new Intent(Intent.ACTION_VIEW, Utilities.uriMaker("www.youtube.com", path, "v",strings[pos][0]+""));
                                startActivity(intent);
                                //Log.v(LOG_TAG, url.toString());
                            }
                        });

                        trailerLayout.addView(itemView);

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
                    (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }


}
