package com.example.astri.popularmovies.view;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.astri.popularmovies.R;
import com.example.astri.popularmovies.data.FavoriteMoviesContract;
import com.example.astri.popularmovies.listener.OnLoadingFragmentListener;
import com.example.astri.popularmovies.model.Movie;
import com.example.astri.popularmovies.model.Review;
import com.example.astri.popularmovies.model.Video;
import com.example.astri.popularmovies.service.MoviesIntentService;
import com.example.astri.popularmovies.utilities.AppConstants;
import com.example.astri.popularmovies.utilities.Utils;

@SuppressWarnings("ALL")


public class DetailsFragment extends Fragment {

    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    public static final String ARG_MOVIE = "arg_movie";
    private Movie movie;
    private static final String SAVE_MOVIE = "save_movie";
    private static final String SAVE_FAVORITE_MOVIE = "save_favorite_movie";
    private static final String SAVE_FAVORITE_SORT = "save_favorite_sort";
    private static final String SAVE_FULLY_LOADED = "save_fully_loaded";
    private static final String SAVE_VIDEOS_EXPANDED = "save_videos_expanded";
    private static final String SAVE_REVIEWS_EXPANDED = "save_reviews_expanded";
    private static final String SAVE_SHARE_MENU_VISIBILITY = "save_share_menu_visibility";

    private final ResponseReceiver mReceiver = new ResponseReceiver();
    private Context mContext;
    private Movie mMovie;
    private LinearLayout mVideosExpandable;
    private LinearLayout mVideosContainer;
    private LinearLayout mReviewsExpandable;
    private LinearLayout mReviewsContainer;
    private ShareActionProvider mShareActionProvider;
    private MenuItem mShareMenuItem;
    private ImageView mPosterImageView;
    private OnLoadingFragmentListener mLoadingListener;
    private boolean mIsFavoriteMovie;
    private boolean mIsFavoriteSort;
    private boolean mIsFullyLoaded;
    private boolean mVideosExpanded;
    private boolean mReviewsExpanded;
    private boolean mIsShareMenuItemVisible;

    //empty constructor

    public DetailsFragment() {
    }

    //nouvelle instance du fragment details

    public static DetailsFragment newInstance(Movie selectedMovie) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, selectedMovie);
        detailsFragment.setArguments(args);
        return detailsFragment;

    }

    public void onCreate(Bundle savedInstancedState) {
        super.onCreate(savedInstancedState);

        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
            mIsFavoriteMovie = isFavoriteMovie(mContext, mMovie);
            mIsFavoriteSort = isFavoriteSort(mContext);
        }

        setHasOptionsMenu(true);
    }


    // Method that checks if current Sort preference is set to Favorite
    public static boolean isFavoriteSort(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String currentSort = preferences.getString(ctx.getString(R.string.pref_sort_order_key),
                ctx.getString(R.string.pref_popular_value));
        return TextUtils.equals(currentSort, ctx.getString(R.string.pref_favorites_value));

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_MOVIE, mMovie);
        outState.putBoolean(SAVE_FAVORITE_MOVIE, mIsFavoriteMovie);
        outState.putBoolean(SAVE_FAVORITE_SORT, mIsFavoriteSort);
        outState.putBoolean(SAVE_FULLY_LOADED, mIsFullyLoaded);
        outState.putBoolean(SAVE_VIDEOS_EXPANDED, mVideosExpanded);
        outState.putBoolean(SAVE_REVIEWS_EXPANDED, mReviewsExpanded);
        outState.putBoolean(SAVE_SHARE_MENU_VISIBILITY, mIsShareMenuItemVisible);
    }

    private boolean isFavoriteMovie(Context context, Movie movie) {
        String movieID = movie.getId();
        Cursor cursor =context.getContentResolver().query(FavoriteMoviesContract.MoviesEntry
                        .CONTENT_URI, null,
                FavoriteMoviesContract.MoviesEntry._ID + " = " + movieID, null, null);
        if (cursor != null && cursor.moveToNext()) {
            int movieIdColumnIndex = cursor.getColumnIndex(FavoriteMoviesContract.MoviesEntry._ID);
            if (TextUtils.equals(movieID, cursor.getString(movieIdColumnIndex))) {
                return true;
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return false;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mShareMenuItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider
                (mShareMenuItem);

        setShareMenuItemAction();
        super.onPrepareOptionsMenu(menu);
    }

    private void setShareMenuItemAction() {
        if (mMovie != null && mMovie.getVideos() != null && mMovie.getVideos().length > 0) {
            String videoKey = mMovie.getVideos()[0].getVideo_key();
            if (!TextUtils.isEmpty(videoKey) && mShareActionProvider != null
                    && mShareMenuItem != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, AppConstants.YOUTUBE_BASE_URL + videoKey);
                mShareActionProvider.setShareIntent(shareIntent);
                mShareMenuItem.setVisible(true);
            }
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (mMovie == null) {
            return null;
        }

        // Restore objects value
        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(SAVE_MOVIE);
            mIsFavoriteMovie = savedInstanceState.getBoolean(SAVE_FAVORITE_MOVIE);
            mIsFavoriteSort = savedInstanceState.getBoolean(SAVE_FAVORITE_SORT);
            mIsFullyLoaded = savedInstanceState.getBoolean(SAVE_FULLY_LOADED);
            mVideosExpanded = savedInstanceState.getBoolean(SAVE_VIDEOS_EXPANDED);
            mReviewsExpanded = savedInstanceState.getBoolean(SAVE_REVIEWS_EXPANDED);
            mIsShareMenuItemVisible = savedInstanceState.getBoolean(SAVE_SHARE_MENU_VISIBILITY);
        }

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        mPosterImageView = view.findViewById(R.id.poster);

        mVideosContainer = view.findViewById(R.id.videos_container);
        mVideosExpandable = view.findViewById(R.id.movies_expand);
        mReviewsContainer = view.findViewById(R.id.reviews_container);
        mReviewsExpandable = view.findViewById(R.id.reviews_expand);

        setExpandListener();

        Glide.with(mContext).load(mMovie.getPosterUri()).dontAnimate().into(mPosterImageView);

       // ImageView posterView = view.findViewById(R.id.poster);
       // Glide.with(getActivity()).load(movie.getPosterUri()).into(posterView);

        TextView titleView = view.findViewById(R.id.title_content);
        titleView.setText(movie.getTitle());

        TextView releaseDateView = view.findViewById(R.id.release_date_content);
        releaseDateView.setText(movie.getReleaseDate());

        TextView averageView = view.findViewById(R.id.vote_average_content);
        averageView.setText(movie.getVoteAverage());

        TextView overviewView = view.findViewById(R.id.overview_content);

        // default text: @string/overview_not_available
        if (!TextUtils.isEmpty(movie.getOverview())) {
            overviewView.setText(movie.getOverview());
        }

        ImageButton starButton = view.findViewById(R.id.rated_star_button);
        starButton.setOnClickListener(mStarButtonOnClickListener);

        if (mIsFavoriteMovie) {
            starButton.setImageResource(R.mipmap.ic_star_full);
        } else {
            starButton.setImageResource(R.mipmap.ic_star_empty);
        }

        starButton.setVisibility(View.VISIBLE);

        FrameLayout detailFrame = view.findViewById(R.id.details_frame);
        detailFrame.setVisibility(View.VISIBLE);

        populateVideosLayout(mContext);
        populateReviewsLayout(mContext);
        return view;
    }



    public static DetailsFragment newInstance() {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }




    // BroadcastReceiver for network call
    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(AppConstants.ACTION_EXTRA_INFO_RESULT)
                    && intent.hasExtra(MoviesIntentService.EXTRA_INFO_VIDEOS_RESULT)
                    && intent.hasExtra(MoviesIntentService.EXTRA_INFO_REVIEWS_RESULT)) {

                Video[] videos = (Video[]) intent.getParcelableArrayExtra(MoviesIntentService
                        .EXTRA_INFO_VIDEOS_RESULT);
                Review[] reviews = (Review[]) intent.getParcelableArrayExtra(MoviesIntentService
                        .EXTRA_INFO_REVIEWS_RESULT);

                mMovie.setVideos(videos);
                mMovie.setReviews(reviews);

                setExpandListener();
                populateVideosLayout(mContext);
                populateReviewsLayout(mContext);
                setShareMenuItemAction();
            } else {
                Toast.makeText(mContext, R.string.toast_failed_to_retrieve_data,Toast.LENGTH_SHORT).show();
            }

            if (mLoadingListener != null) {
                mLoadingListener.onLoadingDisplay(true, false);
            }

            mIsFullyLoaded = true;
        }
    }

    //cette méthode charge les avis dans le cadre expansible reservé pour contenir les avis apres une requete de l'utilisateur

    private void populateReviewsLayout(Context context) {

        Review[] reviews = mMovie.getReviews();

        if (mReviewsContainer != null && mReviewsExpandable != null) {
            if (reviews != null && reviews.length > 0) {
                if (mReviewsContainer.getChildCount() > 0) {
                    mReviewsContainer.removeAllViews();
                }

                LayoutInflater layoutInflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (Review review : reviews) {
                    LinearLayout reviewLayout = (LinearLayout) layoutInflater.inflate(R.layout.review_item, null);
                    TextView authorTextView = reviewLayout.findViewById(R.id.author_name);
                    TextView contentTextView = reviewLayout.findViewById(R.id.review_content);
                    authorTextView.setText(review.getReview_author());
                    contentTextView.setText(review.getReview_content());
                    mReviewsContainer.addView(reviewLayout);
                }

                TextView reviewsHeader = mReviewsExpandable
                        .findViewById(R.id.reviews_header);
                reviewsHeader.setText(String.format(getString(R.string.header_reviews),reviews.length));
                ImageView expandIndicator = mReviewsExpandable.findViewById(R.id.reviews_expand_indicator);
                setExpandIndicator(expandIndicator, mReviewsExpanded);

                if (mReviewsExpanded) {
                    mReviewsContainer.setVisibility(View.VISIBLE);
                } else {
                    mReviewsContainer.setVisibility(View.GONE);
                }

            } else {
                TextView reviewsHeader = mReviewsExpandable.findViewById(R.id.reviews_header);
                reviewsHeader.setText(String.format(getString(R.string.header_reviews), 0));
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        if (mMovie != null) {
            if (mReceiver != null) {
                LocalBroadcastManager.getInstance(mContext)
                        .registerReceiver(mReceiver, new IntentFilter(AppConstants
                                .ACTION_EXTRA_INFO_RESULT));
            }
            if (!mIsFullyLoaded && !mIsFavoriteSort) {
                Intent intent = new Intent(mContext, MoviesIntentService.class);
                intent.setAction(AppConstants.ACTION_EXTRA_INFO_REQUEST);
                intent.putExtra(MoviesIntentService.EXTRA_INFO_MOVIE_ID, mMovie.getId());
                mContext.startService(intent);

                if (mLoadingListener != null) {
                    mLoadingListener.onLoadingDisplay(true, true);
                }
            }
        }
    }




    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
        }
    }




    // Listener to handle custom expandable layout. This layout is used to store Videos and Reviews.
    private final View.OnClickListener mExpandableLayoutOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (view.getId() == R.id.movies_expand) {
                if (mVideosContainer != null && mVideosExpandable != null) {
                    ImageView expandIndicator = mVideosExpandable
                            .findViewById(R.id.videos_expand_indicator);
                    if (mVideosContainer.getVisibility() == View.GONE) {
                        mVideosContainer.setVisibility(View.VISIBLE);
                        mVideosExpanded = true;
                        setExpandIndicator(expandIndicator, mVideosExpanded);
                    } else {
                        mVideosContainer.setVisibility(View.GONE);
                        mVideosExpanded = false;
                        setExpandIndicator(expandIndicator, mVideosExpanded);
                    }
                }
            } else if (view.getId() == R.id.reviews_expand) {
                if (mReviewsContainer != null && mReviewsExpandable != null) {
                    ImageView expandIndicator = mReviewsExpandable
                            .findViewById(R.id.reviews_expand_indicator);
                    if (mReviewsContainer.getVisibility() == View.GONE) {
                        mReviewsContainer.setVisibility(View.VISIBLE);
                        mReviewsExpanded = true;
                        setExpandIndicator(expandIndicator, mReviewsExpanded);
                    } else {
                        mReviewsContainer.setVisibility(View.GONE);
                        mReviewsExpanded = false;
                        setExpandIndicator(expandIndicator, mReviewsExpanded);
                    }
                }
            }
        }
    };

    // Listener to handle Video buttons. It launches YouTube/Browser.
    private final View.OnClickListener mVideoButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (view.getTag() instanceof String) {
                String videoId = (String) view.getTag();
                try {
                    Intent videoIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(AppConstants.YOUTUBE_BASE_URL + videoId));
                    startActivity(videoIntent);

                } catch (ActivityNotFoundException ex) {
                    Log.d(LOG_TAG, "ActivityNotFoundException. Could not find activity to handle " +
                            "this intent.");
                    ex.printStackTrace();
                }
            }
        }
    };



    // Method to set the expandable layout listener
    private void setExpandListener() {
        if (mMovie.getVideos() != null && mMovie.getVideos().length > 0) {
            mVideosExpandable.setOnClickListener(mExpandableLayoutOnClickListener);
        } else {
            mVideosExpandable.setOnClickListener(null);
        }

        if (mMovie.getReviews() != null && mMovie.getReviews().length > 0) {
            mReviewsExpandable.setOnClickListener(mExpandableLayoutOnClickListener);
        } else {
            mReviewsExpandable.setOnClickListener(null);
        }
    }



    // Method to set Background Resource based on current state of expandable layout
    private void setExpandIndicator(ImageView expandIndicator, boolean isExpanded) {
        if (isExpanded) {
            expandIndicator.setBackgroundResource(R.mipmap.ic_collapse);
        } else {
            expandIndicator.setBackgroundResource(R.mipmap.ic_expand);
        }
    }


    //cette méthode charge les videos dans le cadre expansible reservé pour contenir les videos apres une requete de l'utilisateur

    private void populateVideosLayout(Context context) {

        Video[] videos = mMovie.getVideos();

        if (mVideosContainer != null && mVideosExpandable != null) {
            if (videos != null && videos.length > 0) {
                if (mVideosContainer.getChildCount() > 0) {
                    mVideosContainer.removeAllViews();
                }

                LayoutInflater layoutInflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int i = 0; i < videos.length; i++) {
                    LinearLayout videoLayout = (LinearLayout) layoutInflater.inflate(R.layout
                            .video_item, null);
                    Button videoButton = videoLayout.findViewById(R.id.video_button);
                    videoButton.setText(String.format(context.getString(R.string.trailer_item),
                            i + 1));
                    // Set View's tag with YouTube video id
                    videoButton.setTag(videos[i].getVideo_key());
                    videoButton.setOnClickListener(mVideoButtonOnClickListener);
                    mVideosContainer.addView(videoLayout);
                }

                TextView reviewsHeader = mVideosExpandable.findViewById(R.id.video_header);
                reviewsHeader.setText(String.format(getString(R.string.header_videos),videos.length));
                ImageView expandIndicator = mVideosExpandable.findViewById(R.id.videos_expand_indicator);
                setExpandIndicator(expandIndicator, mVideosExpanded);

                if (mVideosExpanded) {
                    mVideosContainer.setVisibility(View.VISIBLE);
                } else {
                    mVideosContainer.setVisibility(View.GONE);
                }

            } else {

                TextView reviewsHeader = mVideosExpandable.findViewById(R.id.video_header);
                reviewsHeader.setText(String.format(getString(R.string.header_videos), 0));
            }
        }

    }


    // Listener to handle star button clicks. This button adds and remove movies from
    // content provider
    private final View.OnClickListener mStarButtonOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {

            // Can't save it to favorites db if movie poster is not ready yet
            if (mPosterImageView != null && !Utils.hasImage(mPosterImageView)) {
                Toast.makeText(mContext, R.string.please_wait_poster_download,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (mIsFavoriteMovie) {
                if (removeFavoriteMovie(mMovie) > 0) {
                    Toast.makeText(mContext, R.string.success_remove_favorites, Toast
                            .LENGTH_SHORT)
                            .show();
                    ((ImageButton) view).setImageResource(R.mipmap.ic_star_empty);

                    // Delete poster image stored in internal storage
                    Utils.deleteFileFromInternalStorage(mContext, mMovie.getId());

                    mIsFavoriteMovie = false;
                } else {
                    Toast.makeText(mContext, R.string.fail_remove_favorites,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                if (addFavoriteMovie(mMovie) != null) {
                    Toast.makeText(mContext, R.string.success_add_favorites, Toast
                            .LENGTH_SHORT).show();
                    ((ImageButton) view).setImageResource(R.mipmap.ic_star_full);

                    // Save poster image to internal storage
                    Bitmap posterBitmap = Utils.getBitmapFromImageView(mPosterImageView);
                    Utils.saveBitmapToInternalStorage(mContext, posterBitmap, mMovie.getId());

                    mIsFavoriteMovie = true;
                } else {
                    Toast.makeText(mContext,"fail_adding_to_favorites", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    //Method that adds a movie to the content provider
    private Uri addFavoriteMovie(Movie movie) {

        Uri movieReturnUri = null;
        try {
            ContentValues movieContentValues = createMovieValues(movie);
            movieReturnUri = mContext.getContentResolver().insert(FavoriteMoviesContract
                    .MoviesEntry
                    .CONTENT_URI, movieContentValues);

            if (movie.getVideos() != null && movie.getVideos().length > 0) {
                ContentValues[] videosContentValuesArray = createVideosValues(movie);
                mContext.getContentResolver().bulkInsert(FavoriteMoviesContract.VideosEntry
                        .CONTENT_URI, videosContentValuesArray);
            }

            if (movie.getReviews() != null && movie.getReviews().length > 0) {
                ContentValues[] reviewContentValuesArray = createReviewsValues(movie);
                mContext.getContentResolver().bulkInsert(FavoriteMoviesContract.ReviewsEntry
                        .CONTENT_URI, reviewContentValuesArray);
            }
        } catch (SQLException e) {
            Log.d(LOG_TAG, "SQLException while adding movies to Favorite db");
            e.printStackTrace();
        }

        return movieReturnUri;
    }


    //this method  creates  reviews content values
    private ContentValues[] createReviewsValues(Movie movie) {
        Review[] reviews = mMovie.getReviews();
        ContentValues[] reviewContentValuesArray = new ContentValues[reviews.length];
        for (int i = 0; i < reviews.length; i++) {
            reviewContentValuesArray[i] = new ContentValues();
            reviewContentValuesArray[i].put(FavoriteMoviesContract.ReviewsEntry._ID, reviews[i]
                    .getReview_id());
            reviewContentValuesArray[i].put(FavoriteMoviesContract.ReviewsEntry.COLUMN_MOVIE_ID,
                    movie.getId());
            reviewContentValuesArray[i].put(FavoriteMoviesContract.ReviewsEntry.COLUMN_AUTHOR,
                    reviews[i].getReview_author());
            reviewContentValuesArray[i].put(FavoriteMoviesContract.ReviewsEntry.COLUMN_CONTENT,
                    reviews[i].getReview_content());
        }

        return reviewContentValuesArray;
    }


    //this method creates movie content values array
    private ContentValues[] createVideosValues(Movie movie) {

        Video[] videos = mMovie.getVideos();
        ContentValues[] videoContentValuesArray = new ContentValues[videos.length];
        for (int i = 0; i < videos.length; i++) {
            videoContentValuesArray[i] = new ContentValues();
            videoContentValuesArray[i].put(FavoriteMoviesContract.VideosEntry._ID, videos[i].getVideo_id());
            videoContentValuesArray[i].put(FavoriteMoviesContract.VideosEntry.COLUMN_MOVIE_ID, movie.getId());
            videoContentValuesArray[i].put(FavoriteMoviesContract.VideosEntry.COLUMN_KEY, videos[i].getVideo_key());
            videoContentValuesArray[i].put(FavoriteMoviesContract.VideosEntry.COLUMN_NAME, videos[i].getVideo_name());
        }

        return videoContentValuesArray;
    }



    //this method  creates a movie content values
    private ContentValues createMovieValues(Movie movie) {
        ContentValues movieContentValues = new ContentValues();
        movieContentValues.put(FavoriteMoviesContract.MoviesEntry._ID, Integer.parseInt(movie.getId()));
        movieContentValues.put(FavoriteMoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
        movieContentValues.put(FavoriteMoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        movieContentValues.put(FavoriteMoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        movieContentValues.put(FavoriteMoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
        movieContentValues.put(FavoriteMoviesContract.MoviesEntry.COLUMN_POSTER_URI, movie.getPosterUri().toString());
        return movieContentValues;
    }


    // Method that removes a Movie from content provider
    private int removeFavoriteMovie(Movie movie) {

            int moviesRemoved = mContext.getContentResolver().delete(FavoriteMoviesContract
                            .MoviesEntry.CONTENT_URI,
                    FavoriteMoviesContract.MoviesEntry._ID + " = ?", new String[]{movie.getId()});

            return moviesRemoved;
    }

}
