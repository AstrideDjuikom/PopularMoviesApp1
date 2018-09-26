package com.example.astri.popularmovies.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.astri.popularmovies.BuildConfig;
import com.example.astri.popularmovies.R;
import com.example.astri.popularmovies.data.FavoriteMoviesContract;
import com.example.astri.popularmovies.listener.OnLoadingFragmentListener;
import com.example.astri.popularmovies.listener.OnMoviesListFragmentListener;
import com.example.astri.popularmovies.model.Movie;
import com.example.astri.popularmovies.service.MoviesIntentService;
import com.example.astri.popularmovies.utilities.AppConstants;
import com.example.astri.popularmovies.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MoviesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String STATE_LAYOUT_MANAGER = "state_recycler_view";
    private static final String STATE_MOVIES_LIST = "state_movies_list";
    private static final String SAVE_LAST_UPDATE_ORDER = "save_last_update_order";
    private static final String SAVE_LAST_SORT_ORDER = "save_last_sort_order";
    private static final int LOADER_FAVORITE_MOVIES = 1;

    private final ResponseReceiver mReceiver = new ResponseReceiver();
    private Context mContext;
    private OnMoviesListFragmentListener mOnMoviesListFragmentListener;
    private OnLoadingFragmentListener mOnLoadingFragmentListener;
    private FavoriteMoviesAdapter mFavoriteMoviesAdapter;
    private MoviesAdapter mMoviesAdapter;
    private ArrayList<Movie> mMoviesList;
    private String mLastUpdateOrder;
    private String mLastSortOrder;
    private DynamicSpanCountRecyclerView mRecyclerView;
    private Parcelable mLayoutManager;


    //empty constructor
    public MoviesListFragment() {
    }

    // Create new Fragment instance
    public static MoviesListFragment newInstance() {
        MoviesListFragment fragment = new MoviesListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utils.isFavoriteSort(mContext)) {
            if (savedInstanceState == null) {
                updateMoviesList();
            }
        }
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_LAST_UPDATE_ORDER, mLastUpdateOrder);
        outState.putString(SAVE_LAST_SORT_ORDER, mLastSortOrder);

        if (mRecyclerView != null) {
            outState.putParcelable(STATE_LAYOUT_MANAGER, mRecyclerView.getLayoutManager()
                    .onSaveInstanceState());
        }
        if (mMoviesList != null) {
            outState.putParcelableArrayList(STATE_MOVIES_LIST, mMoviesList);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        String currentSortOrder = Utils.getSortPref(mContext);
        chooseAdapter(currentSortOrder);
        updateMoviesListVisibility(currentSortOrder);
        mLastSortOrder = currentSortOrder;

        if (!TextUtils.equals(mLastUpdateOrder, Utils.getSortPref(mContext))) {
            updateMoviesList();
        }

        if (Utils.isFavoriteSort(mContext)) {
            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, false);
            }
        }
    }

    private void updateMoviesListVisibility(String currentSortOrder) {
        if (!TextUtils.equals(mLastSortOrder, currentSortOrder)
                || !TextUtils.equals(mLastUpdateOrder, currentSortOrder)) {
            if (mOnMoviesListFragmentListener != null) {
                mOnMoviesListFragmentListener.onUpdateMoviesListVisibility();
            }
        }
    }

    // Starts AsyncTask to fetch The Movie DB API
    public void updateMoviesList() {
        if (Utils.isInternetConnected(getActivity()) && !Utils.isFavoriteSort(mContext)) {
            String currentSortOrder = Utils.getSortPref(mContext);
            mLastUpdateOrder = currentSortOrder;
            Intent intent = new Intent(mContext, MoviesIntentService.class);
            intent.setAction(AppConstants.ACTION_MOVIES_REQUEST);
            intent.putExtra(MoviesIntentService.EXTRA_MOVIES_SORT, currentSortOrder);
            mContext.startService(intent);

            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, true);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movies_list, container, false);

        if (view instanceof DynamicSpanCountRecyclerView) {
            mRecyclerView = (DynamicSpanCountRecyclerView) view;
            mMoviesList = new ArrayList<>();

            if (savedInstanceState != null) {
                mLastUpdateOrder = savedInstanceState.getString(SAVE_LAST_UPDATE_ORDER);
                mLastSortOrder = savedInstanceState.getString(SAVE_LAST_SORT_ORDER);
                mLayoutManager = savedInstanceState.getParcelable(STATE_LAYOUT_MANAGER);
                mMoviesList = savedInstanceState.getParcelableArrayList(STATE_MOVIES_LIST);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManager);
            }

            chooseAdapter(Utils.getSortPref(mContext));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnMoviesListFragmentListener) {
            mOnMoviesListFragmentListener = (OnMoviesListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMoviesListFragmentListener");
        }

        if (context instanceof OnLoadingFragmentListener) {
            mOnLoadingFragmentListener = (OnLoadingFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoadingFragmentListener");
        }

        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnMoviesListFragmentListener = null;
        mOnLoadingFragmentListener = null;
    }


    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(AppConstants.ACTION_MOVIES_RESULT) && intent.hasExtra
                    (MoviesIntentService.EXTRA_MOVIES_RESULT)) {
                Movie[] movies = (Movie[]) intent.getParcelableArrayExtra(MoviesIntentService
                        .EXTRA_MOVIES_RESULT);

                if (mMoviesAdapter != null && mMoviesList != null && movies != null) {
                    mMoviesAdapter.clearRecyclerViewData();
                    Collections.addAll(mMoviesList, movies);
                    mMoviesAdapter.notifyItemRangeInserted(0, movies.length);
                }
            } else {
                Toast.makeText(mContext, R.string.toast_failed_to_retrieve_data, Toast.LENGTH_SHORT)
                        .show();
            }

            if (mOnLoadingFragmentListener != null) {
                mOnLoadingFragmentListener.onLoadingDisplay(false, false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(mContext)
                    .registerReceiver(mReceiver, new IntentFilter(AppConstants.ACTION_MOVIES_RESULT));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_FAVORITE_MOVIES:
                return new CursorLoader(mContext, FavoriteMoviesContract.MoviesEntry
                        .CONTENT_URI,
                        null, null, null, null);
            default:
                Log.d(LOG_TAG, "Couldn't find loader");
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mFavoriteMoviesAdapter != null) {
            mFavoriteMoviesAdapter.swapCursor(data);
            if (data != null && data.getCount() <= 0) {
                Toast.makeText(mContext, R.string.favorites_empty, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mFavoriteMoviesAdapter != null) {
            mFavoriteMoviesAdapter.swapCursor(null);
        }
    }

    private void chooseAdapter(String currentSortOrder) {

        if (!TextUtils.equals(mLastUpdateOrder, currentSortOrder) && mMoviesAdapter
                != null) {
            mMoviesAdapter.clearRecyclerViewData();
        }

        RecyclerView.Adapter currentAdapter = mRecyclerView.getAdapter();

        if (Utils.isFavoriteSort(mContext, currentSortOrder)
                && !(currentAdapter instanceof FavoriteMoviesAdapter)) {
            mLastUpdateOrder = currentSortOrder;
            mFavoriteMoviesAdapter = new FavoriteMoviesAdapter
                    (mOnMoviesListFragmentListener);
            mRecyclerView.setAdapter(mFavoriteMoviesAdapter);

            getLoaderManager().initLoader(LOADER_FAVORITE_MOVIES, null, this);


        } else if (!Utils.isFavoriteSort(mContext, currentSortOrder)
                && !(currentAdapter instanceof MoviesAdapter)) {

            mMoviesAdapter = new MoviesAdapter(mMoviesList,
                    mOnMoviesListFragmentListener);
            mRecyclerView.setAdapter(mMoviesAdapter);

        }

        // Instantiate an empty Details Fragment if sort order has changed
        if (!TextUtils.equals(mLastSortOrder, currentSortOrder)
                && mOnMoviesListFragmentListener != null) {
            mOnMoviesListFragmentListener.onUpdateMovieDetails();
        }
    }
}

