package com.rrdl.cinemapalace.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.rrdl.cinemapalace.R;
import com.rrdl.cinemapalace.data.MoviesContract;
import com.rrdl.cinemapalace.sync.PopularMoviesSyncAdapter;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    boolean mBindingPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_list_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        mBindingPreference = true;
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String value = newValue.toString();

        if (mBindingPreference) {
            if (preference.getKey().equals(getString(R.string.pref_list_key))) {
                PopularMoviesSyncAdapter.syncImmediately(this);
            } else {
                // notify code that selected list may be impacted
                getContentResolver().notifyChange(MoviesContract.MovieEntry.CONTENT_URI, null);
            }
        }

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference)preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(value);
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

}
