package com.example.animerecs.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.animerecs.data.model.Bookmark;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BookmarkRepository {
    private static final String TAG = "BookmarkRepository";
    
    private final FirebaseFirestore db;
    private final MutableLiveData<List<Bookmark>> animeBookmarks = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Bookmark>> mangaBookmarks = new MutableLiveData<>(new ArrayList<>());
    
    public BookmarkRepository() {
        db = FirebaseFirestore.getInstance();
    }
    
    public CollectionReference getBookmarksCollection() {
        return db.collection("bookmarks");
    }
    
    public String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    public void addToBookmarks(Bookmark bookmark) {
        String userId = getCurrentUserId();
        
        if (userId == null) {
            Log.e(TAG, "Cannot add bookmark: User not logged in");
            return;
        }
        
        // Add user ID to bookmark
        bookmark.setUserId(userId);
        
        getBookmarksCollection()
                .add(bookmark)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Bookmark added with ID: " + documentReference.getId());
                    
                    // Update the bookmark with its document ID
                    documentReference.update("documentId", documentReference.getId());
                    
                    // Refresh bookmarks
                    refreshBookmarks();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error adding bookmark", e));
    }
    
    public void removeFromBookmarks(String itemId, String type) {
        String userId = getCurrentUserId();
        
        if (userId == null) {
            Log.e(TAG, "Cannot remove bookmark: User not logged in");
            return;
        }
        
        getBookmarksCollection()
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("type", type)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Bookmark successfully deleted");
                                    
                                    // Refresh bookmarks
                                    refreshBookmarks();
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error deleting bookmark", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error finding bookmark to delete", e));
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
                .addOnSuccessListener(queryDocumentSnapshots -> 
                        callback.accept(!queryDocumentSnapshots.isEmpty()))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking bookmark status", e);
                    callback.accept(false);
                });
    }
    
    public LiveData<List<Bookmark>> getAnimeBookmarks() {
        loadAnimeBookmarks();
        return animeBookmarks;
    }
    
    public LiveData<List<Bookmark>> getMangaBookmarks() {
        loadMangaBookmarks();
        return mangaBookmarks;
    }
    
    private void loadAnimeBookmarks() {
        String userId = getCurrentUserId();
        
        if (userId == null) {
            animeBookmarks.setValue(new ArrayList<>());
            return;
        }
        
        getBookmarksCollection()
                .whereEqualTo("type", Bookmark.TYPE_ANIME)
                .whereEqualTo("userId", userId)
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Bookmark> bookmarks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Bookmark bookmark = document.toObject(Bookmark.class);
                        bookmarks.add(bookmark);
                    }
                    animeBookmarks.setValue(bookmarks);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading anime bookmarks", e));
    }
    
    private void loadMangaBookmarks() {
        String userId = getCurrentUserId();
        
        if (userId == null) {
            mangaBookmarks.setValue(new ArrayList<>());
            return;
        }
        
        getBookmarksCollection()
                .whereEqualTo("type", Bookmark.TYPE_MANGA)
                .whereEqualTo("userId", userId)
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Bookmark> bookmarks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Bookmark bookmark = document.toObject(Bookmark.class);
                        bookmarks.add(bookmark);
                    }
                    mangaBookmarks.setValue(bookmarks);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading manga bookmarks", e));
    }
    
    public void refreshBookmarks() {
        loadAnimeBookmarks();
        loadMangaBookmarks();
    }
} 