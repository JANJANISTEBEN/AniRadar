package com.example.animerecs.data.model;

import java.util.Date;

public class Bookmark {
    public static final String TYPE_ANIME = "anime";
    public static final String TYPE_MANGA = "manga";
    
    private String itemId;
    private String title;
    private String imageUrl;
    private String type; // "anime" or "manga"
    private Date dateAdded;
    private String userId;
    private String documentId;
    
    public Bookmark() {
        this.dateAdded = new Date();
    }
    
    public Bookmark(String itemId, String title, String imageUrl, String type) {
        this.itemId = itemId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.type = type;
        this.dateAdded = new Date();
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
    }
    
    public boolean isAnime() {
        return TYPE_ANIME.equals(type);
    }
    
    public boolean isManga() {
        return TYPE_MANGA.equals(type);
    }
} 