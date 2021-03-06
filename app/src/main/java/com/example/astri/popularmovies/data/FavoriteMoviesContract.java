package com.example.astri.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.provider.UserDictionary.Words.CONTENT_URI;

public class FavoriteMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.astri.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS = "reviews";

            //// ///////////       about MOVIES  //////////////
    public static final class MoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_MOVIES;

        //MOVIES DB
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_URI = "poster_uri";


        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }




           //// ///////////       about VIDEOS  //////////////

    public static final class VideosEntry implements BaseColumns{
        public static final String TABLE_NAME = "videos";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_VIDEOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_VIDEOS;

        //VIDEOS DB
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_KEY = "movie_key";
        public static final String COLUMN_NAME = "movie_name";

        public static Uri buildVideosUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }




    //// ///////////       about REVIEWS  //////////////

    public static final class ReviewsEntry implements BaseColumns{


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_REVIEWS;

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }



}
