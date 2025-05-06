package com.example.animerecs.api.model;

import com.google.gson.annotations.SerializedName;

public class Pagination {
    @SerializedName("current_page")
    private int currentPage;
    
    @SerializedName("last_visible_page")
    private int lastVisiblePage;
    
    @SerializedName("has_next_page")
    private boolean hasNextPage;
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getLastVisiblePage() {
        return lastVisiblePage;
    }
    
    public void setLastVisiblePage(int lastVisiblePage) {
        this.lastVisiblePage = lastVisiblePage;
    }
    
    public boolean hasNextPage() {
        return hasNextPage;
    }
    
    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
} 