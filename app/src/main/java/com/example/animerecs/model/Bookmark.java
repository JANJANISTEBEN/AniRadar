package com.example.animerecs.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;
import java.util.Map;

public class Bookmark {
    public static final String TYPE_ANIME = "anime";
    public static final String TYPE_MANGA = "manga";
    
    @DocumentId
    private String id;
    
    private String userId;
    private String itemId;
    private String itemType; // "anime" or "manga"
    private String title;
    private String imageUrl;
    
    @ServerTimestamp
    private Timestamp createdAt;
    
    // Empty constructor for Firestore
    public Bookmark() {
    }
    
    public Bookmark(String userId, String itemId, String itemType, String title, String imageUrl) {
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.title = title;
        this.imageUrl = imageUrl;
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("itemId", itemId);
        map.put("itemType", itemType);
        map.put("title", title);
        map.put("imageUrl", imageUrl);
        
        if (createdAt != null) {
            map.put("createdAt", createdAt);
        }
        
        return map;
    }
    
    // Getters and Setters
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public boolean isAnime() {
        return TYPE_ANIME.equals(itemType);
    }
    
    public boolean isManga() {
        return TYPE_MANGA.equals(itemType);
    }
}