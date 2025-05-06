package com.example.animerecs.api.model;

import com.google.gson.annotations.SerializedName;

public class AnimeResponse {
    @SerializedName("data")
    private AnimeData data;
    
    public AnimeData getData() {
        return data;
    }
    
    public void setData(AnimeData data) {
        this.data = data;
    }
} 