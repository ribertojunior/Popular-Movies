package com.casasw.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.casasw.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import static com.casasw.popularmovies.R.id.imageView;
import static com.casasw.popularmovies.Utilities.uriMaker;

/**
 * Fragment for DetailActivity
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int LOADER_ID = 15;

    static final String DETAIL_URI = "URI";
    private Uri mUri;


    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }



    /*public void updateContent() {
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

        *//**
         *Static elements
        *//*
        TextView textView = (TextView) getActivity().findViewById(R.id.userRatingText);
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.ReleaseDateText);
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.trailerText);
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.reviewsText);
        textView.setVisibility(View.VISIBLE);


        *//**
         * Dynamic elements
         *//*
        textView = (TextView) getActivity().findViewById(R.id.movieTitleText);
        textView.setText(movie.getTitle());
        textView.setVisibility(View.VISIBLE);

        textView = (TextView) getActivity().findViewById(R.id.overviewText);
        textView.setText(movie.getOverview());
        textView.setTextSize(textSize);


        ImageView imageView = (ImageView) getActivity().findViewById(imageView);
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
                *//*Log.v(LOG_TAG, "New favorited movie list.");
                Iterator<Movie> it = hashMovies.values().iterator();
                while(it.hasNext()){
                    Log.v(LOG_TAG, it.next().getTitle());
                }*//*
                finalListMovies.setMovies(hashMovies);
                complexPreferences.putObject(FMOVIES, finalListMovies);
                complexPreferences.commit();
            }

        });
        imageView.setVisibility(View.VISIBLE);



        FetchTrailersTask task = new FetchTrailersTask(getContext(), getActivity().getLayoutInflater());

        task.execute(movie.getId()+"");
    }*/


    public void onListChanged() {
        String list = Utilities.getMoviesList(getActivity());
        if ( mUri != null ){
            mUri = MovieContract.MovieEntry.CONTENT_URI;
            if (list.equals(getString(R.string.pref_order_favorites_entry))){
                mUri = MovieContract.FavoritesEntry.CONTENT_URI;
            }

            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, MainFragment.getMovieColumns(), null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            ViewHolder viewHolder = new ViewHolder(getView());
            viewHolder.mTitle.setText(data.getString(MainFragment.COL_ORIGINAL_TITLE));
            viewHolder.mOverview.setText(data.getString(MainFragment.COL_OVERVIEW));

            viewHolder.mBackdrop.setAdjustViewBounds(true);
            viewHolder.mBackdrop.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(getContext()).load(
                    uriMaker(
                            data.getString(MainFragment.COL_BACKDROP_PATH),
                            "w342")).into(viewHolder.mBackdrop);

            viewHolder.mVoteAvg.setText(data.getString(MainFragment.COL_VOTE_AVARAGE));
            viewHolder.mDate.setText(data.getString(MainFragment.COL_RELEASE_DATE));
            viewHolder.mStar.setImageResource(android.R.drawable.btn_star_big_off);
            /*
            Testar se filmes está na tabela de favoritos, se sim mudar para star_nig_on
            implementar o clicklistener para adicionar/remover o filme dos favoitos
             */

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class  ViewHolder {
        public final TextView mTitle;
        public final ImageView mBackdrop;
        public final TextView mVoteAvg;
        public final TextView mDate;
        public final ImageView mStar;
        public final TextView mOverview;
        public final LinearLayout mTrailer;
        public final LinearLayout mReview;

        public ViewHolder(View view) {
            mTitle = (TextView) view.findViewById(R.id.movieTitleText);
            mBackdrop = (ImageView) view.findViewById(imageView);
            mVoteAvg = (TextView) view.findViewById(R.id.voteAvgText);
            mDate = (TextView) view.findViewById(R.id.dateText);
            mStar = (ImageView) view.findViewById(R.id.imageViewStar);
            mOverview = (TextView) view.findViewById(R.id.overviewText);
            mTrailer = (LinearLayout) view.findViewById(R.id.viewTrailer);
            mReview = (LinearLayout) view.findViewById(R.id.viewReview);

        }
    }


}
