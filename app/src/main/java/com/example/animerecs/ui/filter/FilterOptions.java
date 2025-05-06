package com.example.animerecs.ui.filter;

import java.util.List;

/**
 * Class to hold all possible filter options for anime and manga.
 * Based on Jikan API filter parameters.
 */
public class FilterOptions {
    private String type;
    private String status;
    private String orderBy;
    private String sort;
    private String season;
    private Float minScore;
    private Float maxScore;
    private List<Integer> genreIds;
    
    public FilterOptions() {
        // Default values
        minScore = 0f;
        maxScore = 10f;
    }
    
    /**
     * @return true if any filter is applied (not using default values)
     */
    public boolean hasActiveFilters() {
        return (type != null && !type.isEmpty()) ||
               (status != null && !status.isEmpty()) ||
               (orderBy != null && !orderBy.isEmpty()) ||
               (season != null && !season.isEmpty()) ||
               (minScore > 0f) ||
               (maxScore < 10f) ||
               (genreIds != null && !genreIds.isEmpty());
    }
    
    /**
     * Creates a comma-separated string of genre IDs
     */
    public String getGenresAsString() {
        if (genreIds == null || genreIds.isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (Integer id : genreIds) {
            sb.append(id).append(",");
        }
        // Remove trailing comma
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    // Getters and setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getOrderBy() {
        return orderBy;
    }
    
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    
    public String getSort() {
        return sort;
    }
    
    public void setSort(String sort) {
        this.sort = sort;
    }
    
    public Float getMinScore() {
        return minScore;
    }
    
    public void setMinScore(Float minScore) {
        this.minScore = minScore;
    }
    
    public Float getMaxScore() {
        return maxScore;
    }
    
    public void setMaxScore(Float maxScore) {
        this.maxScore = maxScore;
    }
    
    public List<Integer> getGenreIds() {
        return genreIds;
    }
    
    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }
    
    public String getSeason() {
        return season;
    }
    
    public void setSeason(String season) {
        this.season = season;
    }
} 