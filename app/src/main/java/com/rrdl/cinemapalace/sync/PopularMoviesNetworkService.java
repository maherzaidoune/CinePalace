package com.rrdl.cinemapalace.sync;



import com.rrdl.cinemapalace.data.Movie;
import com.rrdl.cinemapalace.data.Review;
import com.rrdl.cinemapalace.data.TVSeries;
import com.rrdl.cinemapalace.data.Trailer;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


public interface PopularMoviesNetworkService {

    @GET("/fetch")
    void getAllMovies(Callback<List<Movie>> callback);

    @GET("/discover/tv")
    void getAllTvSeries(@Query("api_key") String apiKey, @Query("language") String language, Callback<List<TVSeries>> callback);

    @GET("/movie/{movie_id}/videos")
    void getTrailers(@Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language, Callback<List<Trailer>> callback);

    @GET("/movie/{movie_id}/reviews")
    void getReviews(@Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language, Callback<List<Review>> callback);

    @GET("/tv/{movie_id}/videos")
    void getTvTrailers(@Path("movie_id") long movieId, @Query("api_key") String apiKey, @Query("language") String language, Callback<List<Trailer>> callback);

}
