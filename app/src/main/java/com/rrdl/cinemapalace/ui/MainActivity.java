package com.rrdl.cinemapalace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rrdl.cinemapalace.R;
import com.rrdl.cinemapalace.sync.PopularMoviesSyncAdapter;


public class MainActivity extends AppCompatActivity implements MoviesListFragment.MovieSelectedCallback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            PopularMoviesSyncAdapter.syncImmediately(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(long id) {
        Log.i("MainActivity","onItemSelected");
        Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsActivity.KEY_MOVIE_ID, id);
        Log.i("MainActivity","intent created");
        startActivity(intent);

    }
}
