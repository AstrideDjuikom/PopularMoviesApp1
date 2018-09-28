package com.example.astri.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.astri.popularmovies.data.FavoriteMoviesContract.MoviesEntry;

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME ="favorites_movies.db";
    private static final int DATABASE_VERSION= 1;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }




    private static final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE "
            + MoviesEntry.TABLE_NAME
            + " (" + MoviesEntry._ID + " TEXT PRIMARY KEY, "
            + MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
            + MoviesEntry.COLUMN_POSTER_URI + " TEXT NOT NULL "
            + " );";

    private static final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE "
            + FavoriteMoviesContract.VideosEntry.TABLE_NAME
            + " (" + FavoriteMoviesContract.VideosEntry._ID + " TEXT PRIMARY KEY, "
            + FavoriteMoviesContract.VideosEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
            + FavoriteMoviesContract.VideosEntry.COLUMN_KEY + " TEXT NOT NULL, "
            + FavoriteMoviesContract.VideosEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + "FOREIGN KEY (" + FavoriteMoviesContract.VideosEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
            MoviesEntry.TABLE_NAME + " (" + MoviesEntry._ID + ") ON DELETE CASCADE"
            + " );";

    private static final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE "
            + FavoriteMoviesContract.ReviewsEntry.TABLE_NAME
            + " (" + FavoriteMoviesContract.ReviewsEntry._ID + " TEXT PRIMARY KEY, "
            + FavoriteMoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
            + FavoriteMoviesContract.ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
            + FavoriteMoviesContract.ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL, "
            + "FOREIGN KEY (" + FavoriteMoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
            MoviesEntry.TABLE_NAME + " (" + MoviesEntry._ID + ") ON DELETE CASCADE"
            + " );";

    private static final String SQL_DROP_MOVIES_TABLE = "DROP TABLE IF EXISTS " +
            MoviesEntry.TABLE_NAME;
    private static final String SQL_DROP_VIDEOS_TABLE = "DROP TABLE IF EXISTS " +
            MoviesEntry.TABLE_NAME;
    private static final String SQL_DROP_REVIEWS_TABLE = "DROP TABLE IF EXISTS " +
            FavoriteMoviesContract.ReviewsEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_VIDEOS_TABLE);
    }
}
