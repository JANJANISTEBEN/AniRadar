package com.example.animerecs.api.model;

import com.google.gson.annotations.SerializedName;

public class MangaResponse {
    @SerializedName("data")
    private MangaData data;
    
    public MangaData getData() {
        return data;
    }
    
    public void setData(MangaData data) {
        this.data = data;
    }
} 