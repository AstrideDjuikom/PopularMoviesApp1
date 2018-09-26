package com.example.astri.popularmovies.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.astri.popularmovies.R;
import com.example.astri.popularmovies.listener.OnMoviesListFragmentListener;
import com.example.astri.popularmovies.model.Movie;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private final List<Movie>mMoviesList;
    private final OnMoviesListFragmentListener mListener;


public MoviesAdapter(List<Movie> moviesItemList, OnMoviesListFragmentListener onMoviesListInteractionListener){
    mMoviesList=moviesItemList;
    mListener=onMoviesListInteractionListener;
}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    holder.mMovieItem=mMoviesList.get(position);
        Glide.with(holder.mMoviePoster.getContext()).load(holder.mMovieItem.getPosterUri()).dontTransform().into(holder.mMoviePoster);
        holder.mMovieTextView.setText(holder.mMovieItem.getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener !=null){
                    mListener.onMoviesSelected(holder.mMovieItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public void clearRecyclerViewData() {
        int size = mMoviesList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mMoviesList.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

//definition et initialisation des elements de notre ViewHolder
        private Movie mMovieItem;
        private final View mView;
        private final TextView mMovieTextView;
        private final ImageView mMoviePoster;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mMoviePoster=(ImageView)itemView.findViewById(R.id.movie_poster);
            mMovieTextView=(TextView)itemView.findViewById(R.id.movie_title);
        }
    }
}
