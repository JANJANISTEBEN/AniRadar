package com.example.animerecs.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPreferences {
    @DocumentId
    private String id;
    
    @PropertyName("user_id")
    private String userId;
    
    @PropertyName("favorite_genres")
    private List<String> favoriteGenres;
    
    @PropertyName("preferred_anime_types")
    private List<String> preferredAnimeTypes;
    
    @PropertyName("preferred_manga_types")
    private List<String> preferredMangaTypes;
    
    @PropertyName("min_score")
    private double minScore;
    
    @PropertyName("created_at")
    private Timestamp createdAt;
    
    @PropertyName("updated_at")
    private Timestamp updatedAt;
    
    // Required empty constructor for Firestore
    public UserPreferences() {
        favoriteGenres = new ArrayList<>();
        preferredAnimeTypes = new ArrayList<>();
        preferredMangaTypes = new ArrayList<>();
        minScore = 7.0; // Default to 7.0
    }
    
    public UserPreferences(String userId) {
        this();
        this.userId = userId;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }
    
    // Convert to Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("favorite_genres", favoriteGenres);
        map.put("preferred_anime_types", preferredAnimeTypes);
        map.put("preferred_manga_types", preferredMangaTypes);
        map.put("min_score", minScore);
        map.put("created_at", createdAt);
        map.put("updated_at", Timestamp.now());
        return map;
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
    
    public List<String> getFavoriteGenres() {
        return favoriteGenres;
    }
    
    public void setFavoriteGenres(List<String> favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
    }
    
    public void addFavoriteGenre(String genre) {
        if (!favoriteGenres.contains(genre)) {
            favoriteGenres.add(genre);
        }
    }
    
    public void removeFavoriteGenre(String genre) {
        favoriteGenres.remove(genre);
    }
    
    public List<String> getPreferredAnimeTypes() {
        return preferredAnimeTypes;
    }
    
    public void setPreferredAnimeTypes(List<String> preferredAnimeTypes) {
        this.preferredAnimeTypes = preferredAnimeTypes;
    }
    
    public void addPreferredAnimeType(String type) {
        if (!preferredAnimeTypes.contains(type)) {
            preferredAnimeTypes.add(type);
        }
    }
    
    public void removePreferredAnimeType(String type) {
        preferredAnimeTypes.remove(type);
    }
    
    public List<String> getPreferredMangaTypes() {
        return preferredMangaTypes;
    }
    
    public void setPreferredMangaTypes(List<String> preferredMangaTypes) {
        this.preferredMangaTypes = preferredMangaTypes;
    }
    
    public void addPreferredMangaType(String type) {
        if (!preferredMangaTypes.contains(type)) {
            preferredMangaTypes.add(type);
        }
    }
    
    public void removePreferredMangaType(String type) {
        preferredMangaTypes.remove(type);
    }
    
    public double getMinScore() {
        return minScore;
    }
    
    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
} 