package com.rrdl.cinemapalace.data;

import com.google.gson.annotations.SerializedName;

public class Trailer {
    @SerializedName("id")
    public String trailerId;
    @SerializedName("iso_639_1")
    public String iso639;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;
}
