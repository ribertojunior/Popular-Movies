package com.casasw.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.casasw.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

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
    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_LIST,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_URL,
            MovieContract.TrailersEntry.COLUMN_SITE,
            MovieContract.TrailersEntry.COLUMN_NAME,
            MovieContract.TrailersEntry.COLUMN_KEY

    };
    static final int COL_MOVIE_ID = 0;
    static final int COL_ORIGINAL_TITLE = 1;
    static final int COL_RELEASE_DATE = 2;
    static final int COL_POSTER_PATH = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_VOTE_AVERAGE = 5;
    static final int COL_BACKDROP_PATH = 6;
    static final int COL_MOVIE_LIST = 7;
    static final int COL_REVIEW_AUTHOR = 8;
    static final int COL_REVIEW_URL = 9;
    static final int COL_TRAILER_SITE = 10;
    static final int COL_TRAILER_NAME = 11;
    static final int COL_TRAILER_KEY = 12;


    public DetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }

        return inflater.inflate(R.layout.main, container, false);
    }


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
            return new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            ViewHolder viewHolder = new ViewHolder(getView());
            viewHolder.mTitle.setText(data.getString(COL_ORIGINAL_TITLE));
            viewHolder.mOverview.setText(data.getString(COL_OVERVIEW));

            viewHolder.mBackdrop.setAdjustViewBounds(true);
            viewHolder.mBackdrop.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(getContext()).load(
                    uriMaker(
                            data.getString(COL_BACKDROP_PATH).substring(1),
                            "w342")).into(viewHolder.mBackdrop);

            viewHolder.mVoteAvg.setText(data.getString(COL_VOTE_AVERAGE));
            viewHolder.mDate.setText(data.getString(COL_RELEASE_DATE));
            viewHolder.mStar.setImageResource(android.R.drawable.btn_star_big_off);
            Log.v(LOG_TAG, "onLoadFinished: Raw data");
            View item;
            TextView textView;
            ImageView reviewImage;
            HashMap<String, String> unique = new HashMap<>();
            do {
               /*Log.v(LOG_TAG, "onLoadFinished: ---------------");

                Log.v(LOG_TAG, "onLoadFinished: Trailer data: "+
                        data.getString(COL_TRAILER_NAME)+" "+
                        data.getString(COL_TRAILER_SITE)+" "+
                        data.getString(COL_TRAILER_KEY));
                Log.v(LOG_TAG, "onLoadFinished: Review data: "+
                        data.getString(COL_REVIEW_AUTHOR)+" "+
                        data.getString(COL_REVIEW_URL));*/
                if (!unique.containsValue(data.getString(COL_TRAILER_NAME))) {
                    unique.put(data.getString(COL_TRAILER_NAME), data.getString(COL_TRAILER_NAME));
                    item = LayoutInflater.from(getActivity()).inflate(R.layout.view_items, null);
                    textView = (TextView) item.findViewById(R.id.textViewTrailer);
                    textView.setText(" "+getString(R.string.play_trailer)+" "+data.getString(COL_TRAILER_NAME));
                    viewHolder.mTrailer.addView(item);
                }

                if (!unique.containsValue(data.getString(COL_REVIEW_URL))) {
                    unique.put(data.getString(COL_REVIEW_URL),data.getString(COL_REVIEW_URL));
                    item = LayoutInflater.from(getActivity()).inflate(R.layout.view_items, null);
                    reviewImage = (ImageView) item.findViewById(R.id.imageViewPlay);
                    reviewImage.setImageResource(R.drawable.text);
                    textView = (TextView) item.findViewById(R.id.textViewTrailer);
                    textView.setText(getString(R.string.read_pre)+" "+data.getString(COL_REVIEW_AUTHOR)+ getString(R.string.read_pos));
                    viewHolder.mReview.addView(item);
                }


            } while (data.moveToNext());

            /*
            Testar se filmes está na tabela de favoritos, se sim mudar para star_nig_on
            implementar o clicklistener para adicionar/remover o filme dos favoritos
            ------
             Preciso alterar o inner join para ser único (já fiz e já fiz alteraçoes no provider) e preciso testar como preencher a tela evitando repetir dados ou instruções,
             pois com a inner join única teremos registros repetidos com alteração apenas nos campos da reviews e da trailer.
             Penso em um método que edite esses dados, recebe o cursor duplicado e retorna um cv talvez com os dados dos trailer e dos reviews.
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
