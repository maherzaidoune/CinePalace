package com.rrdl.cinemapalace.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rrdl.cinemapalace.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.gridview_poster_item, parent, false);
        PosterItemViewHolder viewHolder = new PosterItemViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final PosterItemViewHolder holder = (PosterItemViewHolder)view.getTag();

        final String movieTitle = cursor.getString(MoviesListFragment.COL_TITLE);
        holder.title.setText(movieTitle);

        final String imageUrl = "http://image.tmdb.org/t/p/w185" + cursor.getString(MoviesListFragment.COL_IMAGE_PATH);
        Glide.with(context).load(imageUrl).into(holder.image);
    }

    public static class PosterItemViewHolder {
        @BindView(R.id.poster_img) ImageView image;
        @BindView(R.id.poster_title) TextView title;

        public PosterItemViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
