package com.example.astri.popularmovies.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.astri.popularmovies.R;
import com.example.astri.popularmovies.model.Movie;
import com.example.astri.popularmovies.utilities.AppConstants;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(AppConstants.EXTRA_MOVIE)) {
                DetailsFragment detailsFragment = DetailsFragment.newInstance((Movie) intent.getParcelableExtra(AppConstants.EXTRA_MOVIE));
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.details_fragment_container, detailsFragment)
                        .commit();
            }
        }
    }
}

