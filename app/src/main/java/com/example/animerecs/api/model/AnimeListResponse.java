package com.example.animerecs.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnimeListResponse {
    @SerializedName("data")
    private List<AnimeData> animeList;
    
    @SerializedName("pagination")
    private Pagination pagination;
    
    public List<AnimeData> getAnimeList() {
        return animeList;
    }
    
    public void setAnimeList(List<AnimeData> animeList) {
        this.animeList = animeList;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    // Debug helper method
    public String toDataString() {
        if (animeList == null) return "null animeList";
        StringBuilder sb = new StringBuilder();
        sb.append("AnimeListResponse: ").append(animeList.size()).append(" items\n");
        for (int i = 0; i < Math.min(3, animeList.size()); i++) {
            AnimeData data = animeList.get(i);
            sb.append(i).append(": ").append(data.getTitle())
              .append(" (").append(data.getType()).append(")\n");
        }
        return sb.toString();
    }
} 