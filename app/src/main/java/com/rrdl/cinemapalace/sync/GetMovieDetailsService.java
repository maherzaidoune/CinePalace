package com.rrdl.cinemapalace.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.rrdl.cinemapalace.data.MoviesContract;
import com.rrdl.cinemapalace.data.Review;
import com.rrdl.cinemapalace.data.Trailer;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
public class GetMovieDetailsService extends IntentService {
    private static final String LOG_TAG = GetMovieDetailsService.class.getSimpleName();
    public static final String MOVIE_ID_QUERY_EXTRA = "movie_id";
    public static final String IS_MOVIE_QUERY_EXTRA = "is_movie";
    private long movieDbID = 0;

    public GetMovieDetailsService() {
        super("GetMovieDetailsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!NetworkHelper.isConnected(this)) {
            return;
        }
        this.movieDbID = intent.getLongExtra(MOVIE_ID_QUERY_EXTRA, 0);
        boolean isMovie = intent.getBooleanExtra(IS_MOVIE_QUERY_EXTRA, true);
        if (movieDbID <= 0) {
            return;
        }
        final PopularMoviesNetworkService popularMoviesNetworkService = NetworkHelper.getMovieRESTAdapter();

        if (isMovie) {
            popularMoviesNetworkService.getTrailers(movieDbID, NetworkHelper.MOVIEDB_API_KEY, Locale.getDefault().getLanguage(), callback);
        } else {
            popularMoviesNetworkService.getTvTrailers(movieDbID, NetworkHelper.MOVIEDB_API_KEY, Locale.getDefault().getLanguage(), callback);
        }

        // there are no reviews for tv shows yet
        if (isMovie) {
            popularMoviesNetworkService.getReviews(movieDbID, NetworkHelper.MOVIEDB_API_KEY, Locale.getDefault().getLanguage(), new Callback<List<Review>>() {
                @Override
                public void success(List<Review> reviews, Response response) {
                    final Vector<ContentValues> cVReviewsVector = new Vector<>(reviews.size());
                    for (final Review review : reviews) {
                        final ContentValues reviewValues = new ContentValues();
                        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, movieDbID);
                        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, review.author);
                        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, review.content);
                        reviewValues.put(MoviesContract.ReviewEntry.COLUMN_REVIEW_ID, review.reviewID);
                        cVReviewsVector.add(reviewValues);
                    }
                    saveContentValuesToDB(MoviesContract.ReviewEntry.CONTENT_URI, cVReviewsVector);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private final Callback<List<Trailer>> callback = new Callback<List<Trailer>>() {
        @Override
        public void success(List<Trailer> trailers, Response response) {
            final Vector<ContentValues> cVTrailersVector = new Vector<>(trailers.size());
            for (final Trailer trailer : trailers) {
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, movieDbID);
                trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.trailerId);
                trailerValues.put(MoviesContract.TrailerEntry.COLUMN_ISO_639, trailer.iso639);
                trailerValues.put(MoviesContract.TrailerEntry.COLUMN_KEY, trailer.key);
                trailerValues.put(MoviesContract.TrailerEntry.COLUMN_NAME, trailer.name);
                trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SITE, trailer.site);
                trailerValues.put(MoviesContract.TrailerEntry.COLUMN_SIZE, trailer.size);
                trailerValues.put(MoviesContract.TrailerEntry.COLUMN_TYPE, trailer.type);
                cVTrailersVector.add(trailerValues);
            }
            saveContentValuesToDB(MoviesContract.TrailerEntry.CONTENT_URI, cVTrailersVector);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    };

    private void saveContentValuesToDB(@NonNull Uri tableUri, @NonNull Vector<ContentValues> cVVector) {
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContentResolver().bulkInsert(tableUri, cvArray);
        }
    }
}
