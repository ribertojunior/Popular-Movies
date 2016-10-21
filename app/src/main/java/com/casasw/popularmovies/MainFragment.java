package com.casasw.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;


public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    ImageAdapter mImageAdapter;
    private static final int LOADER_ID = 14;
    GridView mGridView;
    ArrayList<Movie> mMoviesList;
    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener;
    Context mContext;


    public MainFragment() {
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getContext());
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        String orderBy = sharedPreferences.getString(mContext.getString(R.string.pref_order_key),
                mContext.getString(R.string.pref_order_default));

        //Log.v(LOG_TAG, "order by: "+ orderBy);
        fetchMoviesTask.execute(orderBy);

    }



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mPreferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

                        updateMovies();

                    }
                };
        sharedPreferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        mGridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
        mImageAdapter = new ImageAdapter(getContext(), new ArrayList<Uri>());
        mGridView.setAdapter(mImageAdapter);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = new Movie(mMoviesList.get(i).getId(), mMoviesList.get(i).getTitle(),
                        mMoviesList.get(i).getPoster(), mMoviesList.get(i).getThumbNail(),
                        mMoviesList.get(i).getOverview(), mMoviesList.get(i).getVoteAvg(),
                        mMoviesList.get(i).getReleaseDate());
                DetailFragment detail = (DetailFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_detail);
                if (detail == null) {
                    Intent intent = new Intent(getActivity(),DetailActivity.class);
                    intent.putExtra("EXTRA_MOVIE", movie);
                    intent.putExtra("EXTRA_POSITION",i);
                    startActivity(intent);
                } else {
                    Bundle extras = new Bundle();
                    extras.putParcelable("EXTRA_MOVIE", movie);
                    detail.setArguments(extras);
                    detail.updateContent();
                }


            }
        });



        updateMovies();


        return rootView;
    }

    public Movie getMovie(int position) {
        return mMoviesList.get(position);
    }



    @Override
    public void onResume() {
        super.onResume();
        //updateMovies();
    }

    @Override
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    public void onListChanged() {
        updateMovies();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }
}
