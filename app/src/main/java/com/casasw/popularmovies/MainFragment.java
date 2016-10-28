package com.casasw.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.casasw.popularmovies.data.MovieContract;
import com.casasw.popularmovies.sync.PopularMoviesSyncAdapter;


public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    MovieAdapter mMovieAdapter;
    private static final int LOADER_ID = 14;
    GridView mGridView;
    Context mContext;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_LIST,
            MovieContract.MovieEntry.COLUMN_POSITION
    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_ORIGINAL_TITLE = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_POSTER_PATH = 4;
    static final int COL_OVERVIEW = 5;
    static final int COL_VOTE_AVARAGE = 6;
    static final int COL_BACKDROP_PATH = 7;
    static final int COL_MOVIE_LIST = 8;
    static final int COL_POSITION = 9;

    private String mList;


    public MainFragment() {
    }

    private void updateMovies() {
        PopularMoviesSyncAdapter.syncImmediately(getActivity());

    }
    public void onListChanged() {
        updateMovies();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mList = Utilities.getMoviesList(getActivity());


        mGridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        mGridView.setAdapter(mMovieAdapter);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Movie movie = new Movie(mMoviesList.get(i).getId(), mMoviesList.get(i).getTitle(),
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
                }*/
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieWithTrailersAndReviewsUri(cursor.getLong(COL_MOVIE_ID)));
                }


            }
        });




        return rootView;
    }





    @Override
    public void onResume() {
        super.onResume();
        String list = Utilities.getMoviesList(getActivity());
        if (list != null && !list.equals(mList)) {
           onListChanged();
            mList = list;
        }
    }

    @Override
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String movieList = Utilities.getMoviesList(getActivity());
        Uri movieListUri = MovieContract.MovieEntry.CONTENT_URI;
        String sortOrder = MovieContract.MovieEntry.COLUMN_POSITION + " ASC";
        if (movieList.equals(getString(R.string.pref_order_favorites_entry))) {
            movieListUri = MovieContract.FavoritesEntry.CONTENT_URI;
            sortOrder = MovieContract.FavoritesEntry._ID + " ASC";
        }
        return new CursorLoader(getActivity(), movieListUri, MOVIE_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       /* Log.v(LOG_TAG, "onLoadFinished: data "+data.moveToFirst()+" - "
                + data.getColumnCount()+" - " + data.getCount());*/

        mMovieAdapter.swapCursor(data);

        if (getActivity().findViewById(R.id.detail_container) != null) {
            final int WHAT = 1;
            final Cursor c = data;
            Handler handler = new Handler() {
                @Override
                public void handleMessage (Message msg){
                    c.moveToFirst();
                    Bundle args = new Bundle();
                    args.putParcelable(DetailFragment.DETAIL_URI,
                            MovieContract.MovieEntry.buildMovieUri(c.getLong(COL_MOVIE_ID)));
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(args);
                    FragmentTransaction ft = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.detail_container, detailFragment);
                    ft.commit();
                }
            };
            handler.sendEmptyMessage(WHAT);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }
}
