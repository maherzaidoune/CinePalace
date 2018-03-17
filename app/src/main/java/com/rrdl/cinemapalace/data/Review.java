package com.rrdl.cinemapalace.data;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("id")
    public String reviewID;
    public String author;
    public String content;
}
