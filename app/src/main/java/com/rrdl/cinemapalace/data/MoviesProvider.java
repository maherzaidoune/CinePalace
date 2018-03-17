package com.rrdl.cinemapalace.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;


public class MoviesProvider extends ContentProvider {
    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static final int MOVIES_WITH_LIST_TITLE = 102;
    private static final int TRAILERS = 103;
    private static final int TRAILERS_BY_MOVIE_ID = 104;
    private static final int REVIEWS = 105;
    private static final int REVIEWS_BY_MOVIE_ID = 106;

    private static final UriMatcher uriMather = buildUriMatcher();
    private MoviesDbHelper openHelper;

    private static final SQLiteQueryBuilder trailersByMovieQueryBuilder;
    private static final SQLiteQueryBuilder reviewsByMovieQueryBuilder;

    static {
        trailersByMovieQueryBuilder = new SQLiteQueryBuilder();
        trailersByMovieQueryBuilder.setTables(
                MoviesContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + MoviesContract.TrailerEntry.TABLE_NAME +
                        "." + MoviesContract.TrailerEntry.COLUMN_MOVIE_ID +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID
        );
        reviewsByMovieQueryBuilder = new SQLiteQueryBuilder();
        reviewsByMovieQueryBuilder.setTables(
                MoviesContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MoviesContract.MovieEntry.TABLE_NAME +
                        " ON " + MoviesContract.ReviewEntry.TABLE_NAME +
                        "." + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID +
                        " = " + MoviesContract.MovieEntry.TABLE_NAME +
                        "." + MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID
        );
    }

    private static final String movieByIdSelection =
            MoviesContract.MovieEntry.TABLE_NAME +
                    "." + MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID + " = ? ";

    @Override
    public boolean onCreate() {
        this.openHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMather.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;
            case TRAILERS_BY_MOVIE_ID:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;
            case REVIEWS:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;
            case REVIEWS_BY_MOVIE_ID:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:"+uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMather.match(uri)) {
            case MOVIES: {
                retCursor = openHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_WITH_ID: {
                retCursor = openHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILERS : {
                retCursor = openHelper.getReadableDatabase().query(
                        MoviesContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILERS_BY_MOVIE_ID: {
                retCursor = getTrailersByMovieId(uri, projection, sortOrder);
                break;
            }
            case REVIEWS : {
                retCursor = openHelper.getReadableDatabase().query(
                        MoviesContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEWS_BY_MOVIE_ID: {
                retCursor = getReviewsByMovieId(uri, projection, sortOrder);
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: "+uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMather.match(uri);
        int rowsAffected;

        switch (match) {
            case MOVIES:
            {
                rowsAffected = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case TRAILERS: {
                rowsAffected = db.update(MoviesContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case REVIEWS: {
                rowsAffected = db.update(MoviesContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return rowsAffected;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMather.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MoviesContract.MovieEntry.buildMoviesUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRAILERS: {
                long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MoviesContract.TrailerEntry.buildTrailersUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MoviesContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMather.match(uri);
        int rowsAffected;

        switch (match) {
            case MOVIES: {
                rowsAffected = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRAILERS: {
                rowsAffected = db.delete(MoviesContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEWS: {
                rowsAffected = db.delete(MoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsAffected;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMather.match(uri);

        switch (match) {
            case MOVIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != 1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TRAILERS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != 1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEWS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != 1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }


    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIES_WITH_LIST_TITLE);

        uriMatcher.addURI(authority, MoviesContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(authority, MoviesContract.PATH_TRAILERS + "/#", TRAILERS_BY_MOVIE_ID);

        uriMatcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/#", REVIEWS_BY_MOVIE_ID);

        return uriMatcher;
    }

    private Cursor getTrailersByMovieId(Uri uri, String[] projection, String sortOrder) {
        String selectionMovieId = MoviesContract.TrailerEntry.getMovieIdFromUri(uri);
        String selection = movieByIdSelection;
        String[] selectionArgs = new String[]{selectionMovieId};

        return trailersByMovieQueryBuilder.query(openHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewsByMovieId(Uri uri, String[] projection, String sortOrder) {
        String selectionMovieId = MoviesContract.ReviewEntry.getMovieIdFromUri(uri);
        String selection = movieByIdSelection;
        String[] selectionArgs = new String[]{selectionMovieId};

        return reviewsByMovieQueryBuilder.query(openHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

}
