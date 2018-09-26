/*package com.example.astri.popularmovies.view;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.astri.popularmovies.BuildConfig;
import com.example.astri.popularmovies.R;
import com.example.astri.popularmovies.model.Movie;
import com.example.astri.popularmovies.utilities.AppConstants;

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

import static com.example.astri.popularmovies.view.MoviesListFragment.mMoviesList;

public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
    private MoviesAdapter mMoviesAdapter;
    private List<Movie> movieList;

    FetchMoviesTask(){

    }


    private Movie[] getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {
        movieList = new ArrayList<Movie>();

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray jsonMoviesArray = moviesJson.getJSONArray(AppConstants.JSON_LIST);

        Movie[] moviesArray = new Movie[jsonMoviesArray.length()];

        for (int i = 0; i < jsonMoviesArray.length(); i++) {
            String id = jsonMoviesArray.getJSONObject(i).getString(AppConstants.JSON_ID);
            String title = jsonMoviesArray.getJSONObject(i).getString(AppConstants.JSON_TITLE);
            String releaseDate = jsonMoviesArray.getJSONObject(i).getString(AppConstants
                    .JSON_RELEASE_DATE);
            String voteAverage = jsonMoviesArray.getJSONObject(i).getString(AppConstants
                    .JSON_VOTE_AVERAGE);
            String overview = jsonMoviesArray.getJSONObject(i).getString(AppConstants
                    .JSON_OVERVIEW);
            Uri posterUri = createPosterUri(jsonMoviesArray.getJSONObject(i).getString
                    (AppConstants.JSON_POSTER_PATH));

            moviesArray[i] = new Movie(id, title, releaseDate, voteAverage, overview,
                    posterUri);
        }
        return moviesArray;
    }


    // Creates Uri based on sort order, language, etc
    private Uri createMoviesUri(String sortOrder) {
        Uri builtUri = null;

        //Resources.getSystem().....
        //if (sortOrder.equals(getString(R.string.pref_popular_value)))
        if (sortOrder.toString().equals(R.string.pref_popular_value)) {
            builtUri = Uri.parse(AppConstants.API_POPULAR_MOVIES_BASE_URL);
        } else if (sortOrder.toString().equals(R.string.pref_top_rated_value)) {
            builtUri = Uri.parse(AppConstants.API_TOP_RATED_MOVIES_BASE_URL);
        } else {
            builtUri = Uri.parse(AppConstants.API_POPULAR_MOVIES_BASE_URL);
        }

        Uri apiUri = null;

        // Cette application supporte le francais et l'anglais , fait suivant cet article
        // How to get system's current country: http://stackoverflow
        // .com/questions/4212320/get-the-current-language-in-device


        if (AppConstants.API_FRENCH_LANGUAGE.startsWith(Locale.getDefault().getLanguage())) {
            apiUri = builtUri.buildUpon()
                    .appendQueryParameter(AppConstants.API_KEY_PARAM, BuildConfig
                            .API_KEY)
                    .appendQueryParameter(AppConstants.API_LANGUAGE_PARAM, AppConstants
                            .API_FRENCH_LANGUAGE)
                    .build();
        } else {

            apiUri = builtUri.buildUpon()
                    .appendQueryParameter(AppConstants.API_KEY_PARAM, BuildConfig
                            .API_KEY)
                    .build();
        }

        return apiUri;
    }

    // Method to create poster thumbnail Uri
    private Uri createPosterUri(String posterPath) {
        Uri builtUri = Uri.parse(AppConstants.API_POSTER_MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(AppConstants.API_POSTER_SIZE).appendEncodedPath(posterPath)
                .build();
        return builtUri;
    }


    @Override
    protected Movie[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            Uri moviesUri = createMoviesUri(params[0]);
            URL url = new URL(moviesUri.toString());

            // Create the request to The Movide DB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(DetailsFragment.LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movies data, there's no point in
            // attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(DetailsFragment.LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(DetailsFragment.LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the movies.
        return null;
    }


    @Override
    protected void onPostExecute(Movie[] result ) {
        if (result != null) {
           // mMoviesAdapter.clearRecyclerViewData();
            Collections.addAll(mMoviesList, result);
            //mMoviesAdapter.notifyItemInserted(movieList.size());
           // mMoviesAdapter.notifyItemRangeInserted(0, result.length);

        }
    }
}*/
