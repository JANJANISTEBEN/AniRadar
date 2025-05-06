package com.example.animerecs.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnimeListResponse {
    @SerializedName("data")
    private List<AnimeData> data;
    
    @SerializedName("pagination")
    private Pagination pagination;
    
    public List<AnimeData> getData() {
        return data;
    }
    
    public List<AnimeData> getAnimeList() {
        return data;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    // Debug helper method
    public String toDataString() {
        if (data == null) return "null animeList";
        StringBuilder sb = new StringBuilder();
        sb.append("AnimeListResponse: ").append(data.size()).append(" items\n");
        for (int i = 0; i < Math.min(3, data.size()); i++) {
            AnimeData data = this.data.get(i);
            sb.append(i).append(": ").append(data.getTitle())
              .append(" (").append(data.getType()).append(")\n");
        }
        return sb.toString();
    }
} 