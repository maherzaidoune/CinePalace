package com.rrdl.cinemapalace.data;

import com.google.gson.annotations.SerializedName;

public class TVSeries {
    @SerializedName("id")
    public long movieDbID;
    @SerializedName("original_language")
    public String originalLang;
    public String overview;
    @SerializedName("first_air_date")
    public String firstAirDate;
    @SerializedName("name")
    public String title;
    public double voteAverage;
    public String posterPath;
    public double popularity;
}
