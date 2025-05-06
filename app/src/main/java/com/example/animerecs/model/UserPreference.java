package com.example.animerecs.model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class for user preferences related to anime and manga
 */
public class UserPreference {
    
    // Status constants
    public static final String STATUS_LIKE = "like";
    public static final String STATUS_DISLIKE = "dislike";
    public static final String STATUS_WATCHED = "watched";
    public static final String STATUS_WATCH_LATER = "watch_later";
    public static final String STATUS_READ = "read";
    public static final String STATUS_READ_LATER = "read_later";
    
    // Document fields
    private String id;
    private String userId;
    private String itemId;
    private String itemType;
    private Map<String, Boolean> status;
    private Date createdAt;
    private Date updatedAt;
    
    // Default constructor required for Firestore
    public UserPreference() {
        status = new HashMap<>();
    }
    
    // Constructor with all fields
    public UserPreference(String id, String userId, String itemId, String itemType, 
                          Map<String, Boolean> status, Date createdAt, Date updatedAt) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.status = status != null ? status : new HashMap<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    
    public Map<String, Boolean> getStatus() {
        return status;
    }
    
    public void setStatus(Map<String, Boolean> status) {
        this.status = status != null ? status : new HashMap<>();
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods for status
    public boolean isLiked() {
        return getStatus().getOrDefault(STATUS_LIKE, false);
    }
    
    public boolean isDisliked() {
        return getStatus().getOrDefault(STATUS_DISLIKE, false);
    }
    
    public boolean isWatched() {
        return getStatus().getOrDefault(STATUS_WATCHED, false);
    }
    
    public boolean isWatchLater() {
        return getStatus().getOrDefault(STATUS_WATCH_LATER, false);
    }
    
    public boolean isRead() {
        return getStatus().getOrDefault(STATUS_READ, false);
    }
    
    public boolean isReadLater() {
        return getStatus().getOrDefault(STATUS_READ_LATER, false);
    }
    
    // Convert to Firebase data
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("itemId", itemId);
        map.put("itemType", itemType);
        map.put("status", status);
        
        // Set timestamps
        if (createdAt == null) {
            createdAt = new Date();
        }
        updatedAt = new Date();
        
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        
        return map;
    }
    
    @NonNull
    @Override
    public String toString() {
        return "UserPreference{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", itemType='" + itemType + '\'' +
                ", status=" + status +
                '}';
    }
} 