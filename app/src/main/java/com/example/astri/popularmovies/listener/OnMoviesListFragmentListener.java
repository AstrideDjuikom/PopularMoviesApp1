package com.example.astri.popularmovies.listener;

import com.example.astri.popularmovies.model.Movie;

public interface OnMoviesListFragmentListener {
    void onMoviesSelected(Movie movie);
    void onFavoriteMovieSelected(Movie movie);
    void onUpdateMoviesListVisibility();
    void onUpdateMovieDetails();
}
