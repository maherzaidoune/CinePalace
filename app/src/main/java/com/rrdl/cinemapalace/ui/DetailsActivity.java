package com.rrdl.cinemapalace.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.rrdl.cinemapalace.R;


public class DetailsActivity extends AppCompatActivity {

    public static final String KEY_MOVIE_ID = "movie_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.i("DetailsFragment","onCreate");

        if (savedInstanceState == null) {
            long movieId = getIntent().getLongExtra(KEY_MOVIE_ID, 0);
            Log.i("DetailsFragment key :",String.valueOf(movieId));
            DetailsFragment detailFragment = DetailsFragment.newInstance(movieId);
            Log.i("DetailsFragment :","frgment created");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, detailFragment)
                    .commit();
            Log.i("DetailsFragment :","frgment called");

        }
    }

}
