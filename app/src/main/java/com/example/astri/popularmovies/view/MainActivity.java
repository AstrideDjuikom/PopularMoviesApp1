package com.example.astri.popularmovies.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.astri.popularmovies.R;
import com.example.astri.popularmovies.data.FavoriteMoviesContract;
import com.example.astri.popularmovies.listener.OnLoadingFragmentListener;
import com.example.astri.popularmovies.listener.OnMoviesListFragmentListener;
import com.example.astri.popularmovies.listener.OnNoInternetFragmentListener;
import com.example.astri.popularmovies.model.Movie;
import com.example.astri.popularmovies.utilities.AppConstants;
import com.example.astri.popularmovies.utilities.Utils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnLoadingFragmentListener, OnMoviesListFragmentListener,
        OnNoInternetFragmentListener {

    private boolean mIsTwoPane;
    private View mMoviesFragmentContainer;
    private View mDetailsFragmentContainer;
    private View mNoInternetConnectionFragmentContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesFragmentContainer = findViewById(R.id.id_movies_container);
        mDetailsFragmentContainer = findViewById(R.id.details_fragment_container);
        mNoInternetConnectionFragmentContainer = findViewById(R.id.id_no_internet_container);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mIsTwoPane = mDetailsFragmentContainer != null;

        if (savedInstanceState == null) {

            MoviesListFragment moviesListFragment = MoviesListFragment.newInstance();
            NoInternetFragment noInternetFragment = NoInternetFragment.newInstance();
            fragmentTransaction.add(R.id.id_movies_container, moviesListFragment).add(R.id.id_no_internet_container, noInternetFragment);

            if (mIsTwoPane) {
                DetailsFragment detailFragment = DetailsFragment.newInstance();
                fragmentTransaction.add(R.id.details_fragment_container, detailFragment);
            }

            fragmentTransaction.commit();
        }
    }



    @Override
    public void onFavoriteMovieSelected(Movie movie) {
        if (movie!= null) {
            int movieID = Integer.parseInt(movie.getId());

            // Videos query
            Cursor videosCursor = getContentResolver().query(FavoriteMoviesContract.VideosEntry
                            .CONTENT_URI, null,
                    FavoriteMoviesContract.VideosEntry.COLUMN_MOVIE_ID + " = " + movieID, null,
                    null);
            if (videosCursor != null) {
                try {
                    movie.setVideos(Utils.createVideosFromCursor(videosCursor));
                } finally {
                    if (videosCursor != null) {
                        videosCursor.close();
                    }
                }
            }

            // Reviews query
            Cursor reviewsCursor = getContentResolver().query(FavoriteMoviesContract.ReviewsEntry
                            .CONTENT_URI, null,
                    FavoriteMoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = " + movieID, null,
                    null);
            if (reviewsCursor != null) {
                try {
                    movie.setReviews(Utils.createReviewsFromCursor(reviewsCursor));
                } finally {
                    if (reviewsCursor != null) {
                        reviewsCursor.close();
                    }
                }
            }

            if (mIsTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                DetailsFragment detailsFragment = DetailsFragment.newInstance(movie);
                getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment_container, detailsFragment).commit();
            } else {
                Intent intent = new Intent(this, DetailsActivity.class).putExtra(AppConstants.EXTRA_MOVIE, movie);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onUpdateMoviesListVisibility() {
        changeNoInternetVisibility(Utils.isInternetConnected(this));

    }

    @Override
    public void onUpdateMovieDetails() {
        if (mIsTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DetailsFragment detailFragment = DetailsFragment.newInstance();
            fragmentTransaction.replace(R.id.details_fragment_container, detailFragment);
            fragmentTransaction.commit();
        }

    }

    @Override
    public void onLoadingDisplay(boolean fromDetails, boolean display) {
        Fragment loadingFragment = getSupportFragmentManager().findFragmentByTag(LoadingFragment.FRAGMENT_TAG);
        if (display && loadingFragment == null) {
            loadingFragment = LoadingFragment.newInstance();
            if (fromDetails) {
                getSupportFragmentManager().beginTransaction().add(R.id.details_fragment_container,loadingFragment, LoadingFragment.FRAGMENT_TAG).commit();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.id_movies_container,loadingFragment, LoadingFragment.FRAGMENT_TAG).commit();
            }
        } else if (!display && loadingFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(loadingFragment).commit();
        }
    }



    @Override
    public void onRetry() {

        boolean isInternetConnected = Utils.isInternetConnected(this);

        changeNoInternetVisibility(Utils.isInternetConnected(this));

        if (!isInternetConnected) {
            Toast.makeText(this, R.string.toast_no_internet_connection, Toast.LENGTH_SHORT).show();

        } else {
            MoviesListFragment moviesListFragment = (MoviesListFragment)
                    getSupportFragmentManager().findFragmentById(R.id.id_movies_container);
            moviesListFragment.updateMoviesList();
        }

    }


    @Override
    public void onMoviesSelected(Movie movieItem) {
        if (movieItem != null) {
            if (!Utils.isInternetConnected(this)) {
                Toast.makeText(this, R.string.toast_check_your_internet_connection,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (mIsTwoPane) {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                DetailsFragment detailsFragment = DetailsFragment.newInstance(movieItem);
                getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment_container, detailsFragment).commit();
            } else {
                Intent intent = new Intent(this, DetailsActivity.class).putExtra(AppConstants.EXTRA_MOVIE,movieItem);
                startActivity(intent);
            }
        }
    }

    private void changeNoInternetVisibility(boolean internetConnected) {
        //According to the current internet connection

        if (internetConnected || Utils.isFavoriteSort(this)) {
            mNoInternetConnectionFragmentContainer.setVisibility(View.GONE);
            mMoviesFragmentContainer.setVisibility(View.VISIBLE);

            if (mIsTwoPane) {
                mDetailsFragmentContainer.setVisibility(View.VISIBLE);
            }
        } else {
            mNoInternetConnectionFragmentContainer.setVisibility(View.VISIBLE);
            mMoviesFragmentContainer.setVisibility(View.GONE);

            if (mIsTwoPane) {
                mDetailsFragmentContainer.setVisibility(View.GONE);
            }
        }
    }
}
