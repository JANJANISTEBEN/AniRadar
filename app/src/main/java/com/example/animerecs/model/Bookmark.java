package com.example.animerecs.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Bookmark {
    public static final String TYPE_ANIME = "anime";
    public static final String TYPE_MANGA = "manga";
    
    private String id;
    private String itemId;
    private String title;
    private String imageUrl;
    private String type; // "anime" or "manga"
    private Date dateAdded;
    private String userId;
    private String documentId;
    private String itemType; // Alias for type, for compatibility
    
    public Bookmark() {
        this.dateAdded = new Date();
    }
    
    public Bookmark(String itemId, String title, String imageUrl, String type) {
        this.itemId = itemId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.type = type;
        this.itemType = type; // Set itemType as well for compatibility
        this.dateAdded = new Date();
    }
    
    // Constructor for UserRepository compatibility
    public Bookmark(String userId, String itemId, String itemType, String title, String imageUrl) {
        this.userId = userId;
        this.itemId = itemId;
        this.type = itemType;
        this.itemType = itemType;
        this.title = title;
        this.imageUrl = imageUrl;
        this.dateAdded = new Date();
    }
    
    // Convert to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("itemId", itemId);
        map.put("itemType", type);
        map.put("title", title);
        map.put("imageUrl", imageUrl);
        map.put("dateAdded", dateAdded);
        return map;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
        this.documentId = id; // Set documentId as well for compatibility
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
        this.itemType = type; // Set itemType as well for compatibility
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public void setItemType(String itemType) {
        this.itemType = itemType;
        this.type = itemType; // Set type as well for compatibility
    }
    
    public Date getDateAdded() {
        return dateAdded;
    }
    
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        this.id = documentId; // Set id as well for compatibility
    }
    
    public boolean isAnime() {
        return TYPE_ANIME.equals(type) || TYPE_ANIME.equals(itemType);
    }
    
    public boolean isManga() {
        return TYPE_MANGA.equals(type) || TYPE_MANGA.equals(itemType);
    }
}