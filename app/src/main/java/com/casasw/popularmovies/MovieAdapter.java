package com.casasw.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class MovieAdapter extends CursorAdapter {
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mImageView.setAdjustViewBounds(true);
        viewHolder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String poster_path = cursor.getString(MainFragment.COL_POSTER_PATH);
        Uri posterUri = Utilities.uriMaker(poster_path.substring(1));
        Picasso.with(context).load(posterUri).into(viewHolder.mImageView);

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    static class ViewHolder {
        final ImageView mImageView;

        ViewHolder (View view) {
            mImageView = (ImageView) view.findViewById(R.id.image_view_grid);
        }
    }

}
