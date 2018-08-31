package com.example.astri.popularmovies;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MoviesListFragment.OnMoviesListInteractionListener, NoInternetFragment.OnRetryInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (savedInstanceState == null) {
            if (isInternetConnected()) {
                MoviesListFragment moviesListFragment = getMoviesFragment(getResources()
                        .getConfiguration());
                fragmentTransaction.add(R.id.id_movies_container, moviesListFragment)
                        .commit();
            } else {
                NoInternetFragment noInternetFragment = NoInternetFragment.newInstance();
                fragmentTransaction.add(R.id.id_movies_container, noInternetFragment)
                        .commit();
            }
        } else {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id
                    .id_movies_container);
            if (currentFragment instanceof MoviesListFragment && !isInternetConnected()) {
                NoInternetFragment noInternetFragment = NoInternetFragment.newInstance();
                fragmentTransaction.replace(R.id.id_movies_container, noInternetFragment)
                        .commit();
            } else if (currentFragment instanceof NoInternetFragment && isInternetConnected()) {
                MoviesListFragment moviesListFragment = getMoviesFragment(getResources()
                        .getConfiguration());
                fragmentTransaction.replace(R.id.id_movies_container, moviesListFragment)
                        .commit();
            }
        }


    }

    private MoviesListFragment getMoviesFragment(Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            return MoviesListFragment.newInstance(AppConstants.PORTRAIT_COLUMN_COUNT);
        } else {
            return MoviesListFragment.newInstance(AppConstants.LANDSCAPE_COLUMN_COUNT);
        }
    }


    private boolean isInternetConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }



    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.movies_menu_settings,menu);
            return true;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id==R.id.action_settings){
            Intent intentSettings=new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMoviesListInteraction(Movie item) {
        Intent intentToDetails=new Intent(this, DetailsActivity.class).putExtra(AppConstants.EXTRA_MOVIE,item);
        startActivity(intentToDetails);
    }

    @Override
    public void onRetryInteraction() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.id_movies_container);
        if (currentFragment instanceof NoInternetFragment && isInternetConnected()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MoviesListFragment moviesListFragment = getMoviesFragment(getResources()
                    .getConfiguration());
            fragmentTransaction.replace(R.id.id_movies_container, moviesListFragment)
                    .commit();
        } else if (!isInternetConnected()) {
            Toast.makeText(this," Still no internet connection   retry please" , Toast.LENGTH_SHORT).show();
        }

    }
}
