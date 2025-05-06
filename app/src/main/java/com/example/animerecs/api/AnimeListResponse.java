package com.example.animerecs.api;

import com.example.animerecs.api.model.AnimeData;
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
    
    public void setData(List<AnimeData> data) {
        this.data = data;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    public static class Pagination {
        @SerializedName("last_visible_page")
        private int lastVisiblePage;
        
        @SerializedName("has_next_page")
        private boolean hasNextPage;
        
        @SerializedName("current_page")
        private int currentPage;
        
        @SerializedName("items")
        private Items items;
        
        public int getLastVisiblePage() {
            return lastVisiblePage;
        }
        
        public void setLastVisiblePage(int lastVisiblePage) {
            this.lastVisiblePage = lastVisiblePage;
        }
        
        public boolean isHasNextPage() {
            return hasNextPage;
        }
        
        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }
        
        public int getCurrentPage() {
            return currentPage;
        }
        
        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }
        
        public Items getItems() {
            return items;
        }
        
        public void setItems(Items items) {
            this.items = items;
        }
        
        public static class Items {
            @SerializedName("count")
            private int count;
            
            @SerializedName("total")
            private int total;
            
            @SerializedName("per_page")
            private int perPage;
            
            public int getCount() {
                return count;
            }
            
            public void setCount(int count) {
                this.count = count;
            }
            
            public int getTotal() {
                return total;
            }
            
            public void setTotal(int total) {
                this.total = total;
            }
            
            public int getPerPage() {
                return perPage;
            }
            
            public void setPerPage(int perPage) {
                this.perPage = perPage;
            }
        }
    }
} 