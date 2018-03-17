package com.rrdl.cinemapalace.data;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("id")
    public long movieDbID;
    @SerializedName("adult")
    public boolean isAdult;
    @SerializedName("original_language")
    public String originalLang;
    public String overview;
    public String releaseDate;
    public String title;
    public double voteAverage;
    public String posterPath;
    public double popularity;


}
