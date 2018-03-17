package com.rrdl.cinemapalace.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.fitaleks.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";

    /* Class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;



        public static final String TABLE_NAME = "movies";

        // ID in terms of themoviedb.com
        public static final String COLUMN_MOVIEDB_ID = "id";

        // is this movie adult
        public static final String COLUMN_IS_ADULT = "is_adult";

        // original language
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_lang";

        // title
        public static final String COLUMN_TITLE = "original_title";

        // overview
        public static final String COLUMN_OVERVIEW = "original_overview";

        // release date
        public static final String COLUMN_RELEASE_DATE = "release_date";

        // average vote
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // poster url path
        public static final String COLUMN_POSTER_PATH = "poster_url";

        // poster url path
        public static final String COLUMN_POPULARITY = "popularity";

        // is this data relates to movie or tv
        public static final String COLUMN_IS_MOVIE = "is_movie";


        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static final String TABLE_NAME = "trailers";

        // ID in terms of themoviedb.com
        public static final String COLUMN_TRAILER_ID = "id";
        public static final String COLUMN_ISO_639 = "iso_639";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "reviews";

        // ID in terms of themoviedb.com
        public static final String COLUMN_REVIEW_ID = "id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
