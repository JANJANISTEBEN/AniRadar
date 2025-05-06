package com.example.animerecs.data.model;

public class User {
    private String id;
    private String displayName;
    private String email;
    private String bio;
    private String profileImageUrl;
    private String password; // Note: In a real app, passwords should never be stored in plain text

    public User() {
        // Required empty constructor
    }

    public User(String id, String email, String displayName) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.bio = "";
        this.profileImageUrl = null;
    }

    public User(String id, String displayName, String email, String bio, String profileImageUrl) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // For backward compatibility
    public String getUserId() {
        return id;
    }

    public void setUserId(String userId) {
        this.id = userId;
    }
} 