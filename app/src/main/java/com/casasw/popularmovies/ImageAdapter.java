package com.casasw.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Junior on 23/08/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int count;
    private ArrayList<Uri> imageList;

    public ImageAdapter(Context c, ArrayList<Uri> imageList) {
        mContext = c;
        count = 0;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int i) {
        return imageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view==null){
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(1,1));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8,8,8,8);

        } else {
            imageView = (ImageView) view;
        }
        Picasso.with(mContext).load(imageList.get(i)).into(imageView);
        return imageView;
    }




}
