package com.rrdl.cinemapalace.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rrdl.cinemapalace.data.MoviesContract.MovieEntry;

import static com.rrdl.cinemapalace.data.MoviesContract.*;


public class MoviesDbHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 2;
    public final static String DATABASE_NAME = "popularmovies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIEDB_ID + " INTEGER NOT NULL ," +
                MovieEntry.COLUMN_IS_ADULT+ " INTEGER DEFAULT 0 ," +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT DEFAULT '' , " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT DEFAULT '' , " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_IS_MOVIE + " INTEGER NOT NULL DEFAULT 0 , " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIEDB_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.COLUMN_TRAILER_ID + " STRING NOT NULL ," +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL ," +
                TrailerEntry.COLUMN_ISO_639 + " STRING NOT NULL ," +
                TrailerEntry.COLUMN_KEY + " STRING NOT NULL ," +
                TrailerEntry.COLUMN_NAME + " STRING NOT NULL ," +
                TrailerEntry.COLUMN_SITE + " STRING NOT NULL ," +
                TrailerEntry.COLUMN_SIZE + " STRING NOT NULL ," +
                TrailerEntry.COLUMN_TYPE + " STRING NOT NULL ," +
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_REVIEW_ID + " STRING NOT NULL ," +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL ," +
                ReviewEntry.COLUMN_AUTHOR + " STRING NOT NULL ," +
                ReviewEntry.COLUMN_CONTENT + " STRING NOT NULL ," +
                " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
