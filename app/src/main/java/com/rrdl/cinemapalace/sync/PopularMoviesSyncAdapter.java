package com.rrdl.cinemapalace.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.rrdl.cinemapalace.R;
import com.rrdl.cinemapalace.data.Movie;
import com.rrdl.cinemapalace.data.MoviesContract;
import com.rrdl.cinemapalace.data.TVSeries;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 3;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private PopularMoviesNetworkService popularMoviesNetworkService;

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (!NetworkHelper.isConnected(getContext())) {
            return;
        }
        popularMoviesNetworkService = NetworkHelper.getMovieRESTAdapter();
        popularMoviesNetworkService.getAllMovies(NetworkHelper.MOVIEDB_API_KEY, Locale.getDefault().getLanguage(), new Callback<List<Movie>>() {
            @Override
            public void success(List<Movie> allMovies, Response response) {
                final Vector<ContentValues> cVVector = new Vector<>(allMovies.size());
                for (int i = 0; i < allMovies.size(); ++i) {
                    final Movie movie = allMovies.get(i);

                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID, movie.movieDbID);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movie.originalLang);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.title);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_IS_ADULT, movie.isAdult);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.posterPath);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, movie.popularity);
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_IS_MOVIE, 1);

                    cVVector.add(movieValues);
                }
                if ( cVVector.size() > 0 ) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "Retrieving movies error", error);
            }
        });

        popularMoviesNetworkService.getAllTvSeries(NetworkHelper.MOVIEDB_API_KEY, Locale.getDefault().getLanguage(), new Callback<List<TVSeries>>() {
            @Override
            public void success(List<TVSeries> allTvSeries, Response response) {
                final Vector<ContentValues> cVTvVector = new Vector<>(allTvSeries.size());
                for (int i = 0; i < allTvSeries.size(); ++i) {
                    final TVSeries tvSeries = allTvSeries.get(i);

                    ContentValues tvSeriesValues = new ContentValues();

                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_MOVIEDB_ID, tvSeries.movieDbID);
                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, tvSeries.originalLang);
                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, tvSeries.overview);
                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, tvSeries.firstAirDate);
                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, tvSeries.title);
                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, tvSeries.voteAverage);
                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, tvSeries.posterPath);
                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, tvSeries.popularity);
                    tvSeriesValues.put(MoviesContract.MovieEntry.COLUMN_IS_MOVIE, 0);

                    cVTvVector.add(tvSeriesValues);
                }
                if ( cVTvVector.size() > 0 ) {
                    ContentValues[] cvArray = new ContentValues[cVTvVector.size()];
                    cVTvVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "Retrieving tv series error", error);
            }
        });

    }

    public static void initializeSyncAdapter(final Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    public static void configurePeriodSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        PopularMoviesSyncAdapter.configurePeriodSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

}
