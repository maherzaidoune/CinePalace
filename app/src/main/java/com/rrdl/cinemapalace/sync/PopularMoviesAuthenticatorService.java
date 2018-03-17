package com.rrdl.cinemapalace.sync;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

@SuppressLint("Registered")
public class PopularMoviesAuthenticatorService extends Service {
    private PopularMoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        this.mAuthenticator = new PopularMoviesAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mAuthenticator.getIBinder();
    }
}
