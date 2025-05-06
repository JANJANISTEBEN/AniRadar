package com.example.animerecs.data.repository;

import com.example.animerecs.data.model.User;

public class UserRepository {
    
    private static User currentUser;
    
    public UserRepository() {
        // We no longer automatically create a dummy user
        // This allows the data from Firebase Auth and Firestore to be used instead
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void updateUser(User user) {
        // Update the currentUser with the provided user data
        currentUser = user;
        
        // Save to shared preferences or other persistent storage
        saveUserToPreferences(user);
    }
    
    public boolean isLoggedIn() {
        // Check if the user is logged in
        return currentUser != null;
    }
    
    public void logout() {
        // Clear the user session
        currentUser = null;
        
        // Clear from shared preferences
        clearUserFromPreferences();
    }
    
    /**
     * Creates or updates the current user data
     * @param id User ID (typically from Firebase Auth)
     * @param displayName User's display name
     * @param email User's email address
     * @return The created/updated User object
     */
    public User createOrUpdateUser(String id, String displayName, String email) {
        if (currentUser == null) {
            // Create a new user
            currentUser = new User(id, displayName, email);
        } else {
            // Update existing user
            currentUser.setId(id);
            if (displayName != null && !displayName.isEmpty()) {
                currentUser.setDisplayName(displayName);
            }
            if (email != null && !email.isEmpty()) {
                currentUser.setEmail(email);
            }
        }
        
        // Save to shared preferences
        saveUserToPreferences(currentUser);
        
        return currentUser;
    }
    
    /**
     * Creates or updates the current user data with full details
     * @param id User ID
     * @param displayName User's display name
     * @param email User's email address
     * @param bio User's bio
     * @param profileImageUrl User's profile image URL
     * @return The created/updated User object
     */
    public User createOrUpdateUser(String id, String displayName, String email, String bio, String profileImageUrl) {
        if (currentUser == null) {
            // Create a new user with full details
            currentUser = new User(id, displayName, email, bio, profileImageUrl);
        } else {
            // Update existing user with full details
            currentUser.setId(id);
            if (displayName != null && !displayName.isEmpty()) {
                currentUser.setDisplayName(displayName);
            }
            if (email != null && !email.isEmpty()) {
                currentUser.setEmail(email);
            }
            if (bio != null) {
                currentUser.setBio(bio);
            }
            if (profileImageUrl != null) {
                currentUser.setProfileImageUrl(profileImageUrl);
            }
        }
        
        // Save to shared preferences
        saveUserToPreferences(currentUser);
        
        return currentUser;
    }
    
    private void saveUserToPreferences(User user) {
        // In a real app, save user data to SharedPreferences
        // This is just a placeholder method
    }
    
    private void clearUserFromPreferences() {
        // In a real app, clear user data from SharedPreferences
        // This is just a placeholder method
    }
} 