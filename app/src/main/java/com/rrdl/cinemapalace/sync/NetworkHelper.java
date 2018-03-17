package com.rrdl.cinemapalace.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class NetworkHelper {

    public static final String MOVIEDB_API_KEY = "ee683e1e2e356e9898ca2810fd8966bb";

    public static PopularMoviesNetworkService getMovieRESTAdapter() {
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapterFactory(new MovieDBTypeAdapterFactory())
                .create();

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setConverter(new GsonConverter(gson))
                .build();

        return restAdapter.create(PopularMoviesNetworkService.class);
    }

    public static boolean isConnected(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private static class MovieDBTypeAdapterFactory implements TypeAdapterFactory {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    JsonElement jsonElement = elementAdapter.read(in);
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("results") && jsonObject.get("results").isJsonArray())
                        {
                            jsonElement = jsonObject.get("results");
                        }
                    }

                    return delegate.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }
    }
}
