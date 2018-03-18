package com.rrdl.cinemapalace.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rrdl.cinemapalace.R;
import com.rrdl.cinemapalace.data.MoviesContract;
import com.rrdl.cinemapalace.sync.GetMovieDetailsService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final int DETAILS_MOVIE_LOADER = 0;
    private static final int DETAILS_TRAILERS_LOADER = 1;
    private static final int DETAILS_REVIEWS_LOADER = 2;

    private long mMovieId;
    private boolean mIsMovie;

    private static final String[] DETAILS_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_IS_MOVIE
    };

    private static final String[] TRAILERS_COLUMNS = {
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry._ID,
            MoviesContract.TrailerEntry.COLUMN_MOVIE_ID,
            MoviesContract.TrailerEntry.COLUMN_SITE,
            MoviesContract.TrailerEntry.COLUMN_TYPE,
            MoviesContract.TrailerEntry.COLUMN_KEY,
            MoviesContract.TrailerEntry.COLUMN_NAME
    };

    private static final String[] REVIEWS_COLUMNS = {
            MoviesContract.ReviewEntry.TABLE_NAME + "." + MoviesContract.ReviewEntry._ID,
            MoviesContract.ReviewEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewEntry.COLUMN_CONTENT
    };

    private ShareActionProvider mShareActionProvider;
    private String mMovieShareStr;

    @BindView(R.id.details_movie_poster)
    ImageView poster;
    @BindView(R.id.details_movie_year)
    TextView releaseDate;
    @BindView(R.id.details_movie_rating)
    TextView rating;
    @BindView(R.id.details_movie_overview)
    TextView overview;
    @BindView(R.id.details_trailers_container)
    LinearLayout detailsMovieContainer;
    @BindView(R.id.details_reviews_container)
    LinearLayout detailsReviewContainer;
    @BindView(R.id.details_trailers_card)
    CardView trailersCard;
    @BindView(R.id.details_reviews_card)
    CardView reviewsCard;
    @BindView(R.id.details_fab_like)
    FloatingActionButton fabLike;
    private Unbinder unbinder;

    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    public static DetailsFragment newInstance(long movieId) {
        DetailsFragment detailFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DetailsActivity.KEY_MOVIE_ID, movieId);
        detailFragment.setArguments(bundle);

        return detailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            mMovieId = arguments.getLong(DetailsActivity.KEY_MOVIE_ID);
            Log.i("Details argument :",String.valueOf(mMovieId));

        }
        final View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        Log.i(LOG_TAG,"view created");

        this.fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.changeMovieFavourite(getActivity(), mMovieId);
                fabLike.setImageResource(Utility.isMovieFavourite(getActivity(), mMovieId) ? R.drawable.fab_heart : R.drawable.fab_heart_dislike);
            }
        });
        this.fabLike.setImageResource(Utility.isMovieFavourite(getActivity(), mMovieId) ? R.drawable.fab_heart : R.drawable.fab_heart_dislike);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void updateMovieData() {
        Intent intent = new Intent(getActivity(), GetMovieDetailsService.class);
        intent.putExtra(GetMovieDetailsService.MOVIE_ID_QUERY_EXTRA, this.mMovieId);
        intent.putExtra(GetMovieDetailsService.IS_MOVIE_QUERY_EXTRA, this.mIsMovie);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(DetailsActivity.KEY_MOVIE_ID)
                && mMovieId != 0) {
            getLoaderManager().restartLoader(DETAILS_MOVIE_LOADER, null, this);
            getLoaderManager().restartLoader(DETAILS_TRAILERS_LOADER, null, this);
            getLoaderManager().restartLoader(DETAILS_REVIEWS_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            getLoaderManager().initLoader(DETAILS_MOVIE_LOADER, null, this);
            getLoaderManager().initLoader(DETAILS_TRAILERS_LOADER, null, this);
            getLoaderManager().initLoader(DETAILS_REVIEWS_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieShareStr);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DETAILS_MOVIE_LOADER) {
            Uri movieById = MoviesContract.MovieEntry.buildMoviesUri(this.mMovieId);
            return new CursorLoader(getActivity(),
                    movieById,
                    DETAILS_COLUMNS,
                    null,
                    null,
                    null);
        } else if (id == DETAILS_TRAILERS_LOADER) {
            Uri movieById = MoviesContract.TrailerEntry.buildTrailersUri(this.mMovieId);
            return new CursorLoader(getActivity(),
                    movieById,
                    TRAILERS_COLUMNS,
                    null,
                    null,
                    null);
        } else if (id == DETAILS_REVIEWS_LOADER) {
            Uri movieById = MoviesContract.ReviewEntry.buildReviewUri(this.mMovieId);
            return new CursorLoader(getActivity(),
                    movieById,
                    REVIEWS_COLUMNS,
                    null,
                    null,
                    null);
        }
        throw new UnsupportedOperationException("Unknown id: " + id);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) {
            if (loader.getId() == DETAILS_TRAILERS_LOADER) {
                this.trailersCard.setVisibility(View.GONE);
            } else if (loader.getId() == DETAILS_REVIEWS_LOADER) {
                this.reviewsCard.setVisibility(View.GONE);
            }
            return;
        }
        if (loader.getId() == DETAILS_MOVIE_LOADER) {
            final String imgUrl = "http://image.tmdb.org/t/p/w185" + data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));
            Glide.with(getActivity()).load(imgUrl).into(this.poster);

            final String title = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);

            final String releaseDate = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
            this.releaseDate.setText(releaseDate);

            final double averageRating = data.getDouble(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE));
            this.rating.setText(String.format(getString(R.string.details_rating), averageRating));

            final String overview = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW));
            this.overview.setText(overview);
            this.mIsMovie = data.getInt(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IS_MOVIE)) == 1;
            updateMovieData();
            if (this.trailersCard.getVisibility() == View.GONE) {
                this.mMovieShareStr = String.format(getString(R.string.share_movie), title);
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                }
            }
        } else if (loader.getId() == DETAILS_TRAILERS_LOADER) {
            this.trailersCard.setVisibility(View.VISIBLE);
            detailsMovieContainer.removeAllViews();

            boolean isInited = false;

            do {
                final String name = data.getString(data.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_NAME));
                final String key = data.getString(data.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_KEY));
                if (!isInited) {
                    isInited = true;
                    this.mMovieShareStr = String.format(getString(R.string.share_trailer), "http://www.youtube.com/watch?v=" + key);
                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareMovieIntent());
                    }
                }

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + key));
                            startActivity(intent);
                        }
                    }
                };

                final LayoutInflater inflater = LayoutInflater.from(getContext());
                TextView textView = (TextView) inflater.inflate(R.layout.details_trailer_view, null);
                textView.setText(name);
                textView.setOnClickListener(clickListener);
                final ImageView imageView = new ImageView(getContext());
                final LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(imageViewLayoutParams);
                imageView.setOnClickListener(clickListener);

                final View lineView = new View(getContext());
                lineView.setBackgroundColor(getResources().getColor(android.R.color.black));
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lineView.setLayoutParams(layoutParams);

                detailsMovieContainer.addView(textView);
                detailsMovieContainer.addView(imageView);
                detailsMovieContainer.addView(lineView);

                Glide.with(this)
                        .load("http://img.youtube.com/vi/" + key + "/0.jpg")
                        .into(imageView);

            } while (data.moveToNext());
        } else if (loader.getId() == DETAILS_REVIEWS_LOADER) {
            this.reviewsCard.setVisibility(View.VISIBLE);
            detailsReviewContainer.removeAllViews();

            do {
                final String content = data.getString(data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT));

                final LayoutInflater inflater = LayoutInflater.from(getContext());
                final TextView textView = (TextView) inflater.inflate(R.layout.details_trailer_view, null);
                textView.setText(content);
                textView.setClickable(false);

                View lineView = new View(getActivity());
                lineView.setBackgroundColor(getResources().getColor(android.R.color.black));
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lineView.setLayoutParams(layoutParams);

                detailsReviewContainer.addView(textView);
                detailsReviewContainer.addView(lineView);

            } while (data.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
