package com.casasw.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;

import br.com.kots.mob.complex.preferences.ComplexPreferences;

import static com.casasw.popularmovies.Utilities.uriMaker;

/**
 * Fragment for DetailActivity
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private LinearLayout trailerLayout;
    private LinearLayout reviewLayout;
    private Movie movie;


    public DetailFragment() {
    }



    @Override
    public void setArguments(Bundle args) {
        this.movie = args.getParcelable("EXTRA_MOVIE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateContent(); //must be here to avoid crash
    }



    public void updateContent() {
        final String MPREF = "mPref";
        final String FMOVIES = "fMovies";
        Movie pMovie;
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();

        if(extras==null)
            pMovie = this.movie;

        else
            pMovie = extras.getParcelable("EXTRA_MOVIE");

        if (pMovie==null)
            return;

        int textSize = 15;
        final Movie movie = pMovie;


        final ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(),MPREF, Context.MODE_PRIVATE);

        ListMovies listMovies = complexPreferences.getObject(FMOVIES, ListMovies.class);
        if (listMovies == null || listMovies.getMovies() == null) {
            Log.v(LOG_TAG, "complexPreferences is returning null.");
            listMovies = new ListMovies(new HashMap<Integer, Movie>());
        }
        final HashMap<Integer, Movie> hashMovies = listMovies.getMovies();
        //final HashMap<Integer, Movie> hashMovies = new HashMap<Integer, Movie>();

        /**
         *Static elements
        */
        TextView textView = (TextView) getActivity().findViewById(R.id.userRatingText);
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.ReleaseDateText);
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.trailerText);
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.reviewsText);
        textView.setVisibility(View.VISIBLE);


        /**
         * Dynamic elements
         */
        textView = (TextView) getActivity().findViewById(R.id.movieTitleText);
        textView.setText(movie.getTitle());
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.overviewText);
        textView.setText(movie.getOverview());
        textView.setTextSize(textSize);


        ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //imageView.setPadding(8,8,8,8);
        Picasso.with(getContext()).load(
                uriMaker(
                        movie.getThumbNail(), "w342")).into(imageView);
        //"w92", "w154", "w185", "w342", "w500", "w780"


        textView = (TextView) getActivity().findViewById(R.id.voteAvgText);
        textView.setText(movie.getVoteAvg()+"/10");
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.dateText);
        textView.setText(movie.getReleaseDate().substring(0,4));
        textView.setVisibility(View.VISIBLE);

        imageView = (ImageView) getActivity().findViewById(R.id.imageViewStar);
        imageView.setImageResource(android.R.drawable.btn_star_big_off);
        if (hashMovies.containsKey(movie.getId())){
            imageView.setImageResource(android.R.drawable.btn_star_big_on);
        }
        final ListMovies finalListMovies = listMovies;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg;
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
                /*Log.v(LOG_TAG, "New favorited movie list.");
                Iterator<Movie> it = hashMovies.values().iterator();
                while(it.hasNext()){
                    Log.v(LOG_TAG, it.next().getTitle());
                }*/
                finalListMovies.setMovies(hashMovies);
                complexPreferences.putObject(FMOVIES, finalListMovies);
                complexPreferences.commit();
            }

        });
        imageView.setVisibility(View.VISIBLE);



        FetchTrailersTask task = new FetchTrailersTask(getContext(), getActivity().getLayoutInflater());

        task.execute(movie.getId()+"");
    }


    public void onListChanged(String list) {

    }
}
