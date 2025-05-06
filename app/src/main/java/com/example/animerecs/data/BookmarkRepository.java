package com.example.animerecs.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.animerecs.model.Bookmark;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.function.Consumer;

public class BookmarkRepository {
    
    private final FirebaseFirestore db;
    private final MutableLiveData<List<Bookmark>> allBookmarks;
    
    public BookmarkRepository() {
        db = FirebaseFirestore.getInstance();
        allBookmarks = new MutableLiveData<>();
    }
    
    public LiveData<List<Bookmark>> getAllBookmarks() {
        return allBookmarks;
    }
    
    public String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    public CollectionReference getBookmarksCollection() {
        return db.collection("bookmarks");
    }
    
    public void addToBookmarks(Bookmark bookmark) {
        String userId = getCurrentUserId();
        if (userId == null) return;
        
        bookmark.setUserId(userId);
        getBookmarksCollection().add(bookmark);
    }
    
    public void removeFromBookmarks(String itemId, String type) {
        String userId = getCurrentUserId();
        if (userId == null) return;
        
        getBookmarksCollection()
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("type", type)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }
    
    public void insert(Bookmark bookmark) {
        addToBookmarks(bookmark);
    }
    
    public void delete(Bookmark bookmark) {
        removeFromBookmarks(bookmark.getItemId(), bookmark.getType());
    }
    
    public void checkIfBookmarked(String itemId, String type, Consumer<Boolean> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.accept(false);
            return;
        }
        
        getBookmarksCollection()
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("type", type)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> callback.accept(!querySnapshot.isEmpty()))
                .addOnFailureListener(e -> callback.accept(false));
    }
} 