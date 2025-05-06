package com.example.animerecs.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MangaListResponse {
    @SerializedName("data")
    private List<MangaData> data;
    
    @SerializedName("pagination")
    private Pagination pagination;
    
    public List<MangaData> getData() {
        return data;
    }
    
    public void setData(List<MangaData> data) {
        this.data = data;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
} 