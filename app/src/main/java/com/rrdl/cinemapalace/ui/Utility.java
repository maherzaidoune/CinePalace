package com.rrdl.cinemapalace.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.rrdl.cinemapalace.R;


public class Utility {
    private static final String PREF_KEY_ALL_FAVOURITES = "all_favourites";

    public static String getPrefferedMoviesList(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_list_key), context.getString(R.string.pref_list_popular));
    }

    public static void changeMovieFavourite(final Context context, long movieId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String allFavs = prefs.getString(PREF_KEY_ALL_FAVOURITES, "");
        if (allFavs != null && allFavs.contains(Long.toString(movieId))) {
            allFavs = allFavs.replace(Long.toString(movieId), "").replace(",,", "").trim();
            allFavs = allFavs.startsWith(",") ? allFavs.substring(1) : allFavs;
            allFavs = allFavs.endsWith(",") ? allFavs.substring(0, allFavs.length() - 1) : allFavs;
        } else if (allFavs == null || allFavs.length() == 0){
            allFavs = Long.toString(movieId);
        } else {
            allFavs = allFavs + "," + movieId;
        }
        prefs.edit().putString(PREF_KEY_ALL_FAVOURITES, allFavs).apply();
    }

    public static boolean isMovieFavourite(final Context context, long movieId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String allFavs = prefs.getString(PREF_KEY_ALL_FAVOURITES, "");
        return allFavs != null && allFavs.contains(Long.toString(movieId));
    }

    public static String getAllFavouritesMovieIds(@NonNull final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_KEY_ALL_FAVOURITES, "");

    }
}
