package com.example.astri.popularmovies.view;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.astri.popularmovies.R;
import com.example.astri.popularmovies.data.FavoriteMoviesContract;
import com.example.astri.popularmovies.listener.OnMoviesListFragmentListener;
import com.example.astri.popularmovies.model.Movie;
import com.example.astri.popularmovies.utilities.Utils;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.MyViewHolder> {

    private Cursor myCursor;
    private final OnMoviesListFragmentListener onMoviesListInteractionListener;

    public FavoriteMoviesAdapter(OnMoviesListFragmentListener listener) {
        onMoviesListInteractionListener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public final void onBindViewHolder( final  MyViewHolder holder, final int position) {
        final Cursor cursor = getItem(position);
        onBindViewHolder(holder, cursor);
    }



    private void onBindViewHolder(final MyViewHolder holder, final Cursor cursor) {
        String movieTitle = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract
                .MoviesEntry.COLUMN_TITLE));
        String posterUri = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract
                .MoviesEntry.COLUMN_POSTER_URI));
        int cursorPosition = cursor.getPosition();

        holder.mCursorPosition = cursorPosition;
        holder.mTitle.setText(movieTitle);

        Glide.with(holder.mPosterView.getContext()).load(posterUri)
                .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate().into(holder.mPosterView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMoviesListInteractionListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    cursor.moveToPosition(holder.mCursorPosition);
                    Movie movie = Utils.createMovieFromCursor(cursor);
                    onMoviesListInteractionListener.onFavoriteMovieSelected(movie);
                }
            }
        });
    }

    private Cursor getItem(int position) {
        if (myCursor != null && !myCursor.isClosed()) {
            myCursor.moveToPosition(position);
        }

        return myCursor;
    }

    @Override
    public int getItemCount() {
        if( myCursor!=null ) return myCursor.getCount();
        else return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView mPosterView;
        public final TextView mTitle;

        public int mCursorPosition;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            mPosterView = view.findViewById(R.id.poster);
            mTitle = view.findViewById(R.id.title);
            mCursorPosition = -1;
        }
    }

    public void swapCursor(Cursor cursor) {
        if (myCursor != null) {
            myCursor.close();
        }
        myCursor = cursor;
        notifyDataSetChanged();
    }
}



