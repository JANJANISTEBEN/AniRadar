package com.example.animerecs.data;

import androidx.lifecycle.LiveData;
import java.util.List;
import com.example.animerecs.model.Bookmark;

public class BookmarkRepository {
    
    // Interface for bookmark check callback
    public interface BookmarkCheckCallback {
        void onResult(boolean isBookmarked);
    }
    
    private LiveData<List<Bookmark>> allBookmarks;
    
    public BookmarkRepository() {
        // Initialize database or API connection
    }
    
    public LiveData<List<Bookmark>> getAllBookmarks() {
        return allBookmarks;
    }
    
    public void addToBookmarks(Bookmark bookmark) {
        // Add bookmark logic
    }
    
    public void removeFromBookmarks(String itemId, String type) {
        // Remove bookmark logic
    }
    
    public void insert(Bookmark bookmark) {
        // Insert bookmark
    }
    
    public void delete(Bookmark bookmark) {
        // Delete bookmark
    }
    
    public void deleteById(int id, String type) {
        // Delete by ID and type
    }
    
    public void checkIfBookmarked(String itemId, String type, BookmarkCheckCallback callback) {
        // Check if item is bookmarked
        // callback.onResult(isBookmarked);
    }
    
    public void checkIfBookmarked(String itemId, String type, java.util.function.Consumer<Boolean> callback) {
        // For compatibility with Consumer interface, we use a lambda that won't cause ambiguity
        BookmarkCheckCallback internalCallback = isBookmarked -> callback.accept(isBookmarked);
        checkIfBookmarked(itemId, type, internalCallback);
    }
} 