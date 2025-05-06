package com.example.animerecs.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.animerecs.data.model.Bookmark;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookmarkRepository {
    private static final String TAG = "BookmarkRepository";
    private static final String COLLECTION_BOOKMARKS = "bookmarks";
    
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private MutableLiveData<List<Bookmark>> allBookmarks;
    private MutableLiveData<List<Bookmark>> animeBookmarks;
    private MutableLiveData<List<Bookmark>> mangaBookmarks;
    
    public BookmarkRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        allBookmarks = new MutableLiveData<>(new ArrayList<>());
        animeBookmarks = new MutableLiveData<>(new ArrayList<>());
        mangaBookmarks = new MutableLiveData<>(new ArrayList<>());
    }
    
    public LiveData<List<Bookmark>> getAllBookmarks() {
        if (auth.getCurrentUser() == null) {
            return allBookmarks;
        }
        
        String userId = auth.getCurrentUser().getUid();
        db.collection(COLLECTION_BOOKMARKS)
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening to bookmarks", error);
                        return;
                    }
                    
                    List<Bookmark> bookmarkList = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            Bookmark bookmark = document.toObject(Bookmark.class);
                            bookmark.setDocumentId(document.getId());
                            bookmarkList.add(bookmark);
                        }
                    }
                    
                    allBookmarks.setValue(bookmarkList);
                });
                
        return allBookmarks;
    }
    
    public LiveData<List<Bookmark>> getBookmarksByType(String type) {
        MutableLiveData<List<Bookmark>> result = type.equals("anime") ? animeBookmarks : mangaBookmarks;
        
        if (auth.getCurrentUser() == null) {
            return result;
        }
        
        String userId = auth.getCurrentUser().getUid();
        db.collection(COLLECTION_BOOKMARKS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", type)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening to bookmarks", error);
                        return;
                    }
                    
                    List<Bookmark> bookmarkList = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            Bookmark bookmark = document.toObject(Bookmark.class);
                            bookmark.setDocumentId(document.getId());
                            bookmarkList.add(bookmark);
                        }
                    }
                    
                    result.setValue(bookmarkList);
                });
                
        return result;
    }
    
    public void insert(Bookmark bookmark) {
        if (auth.getCurrentUser() == null) {
            return;
        }
        
        // Ensure userId is set
        bookmark.setUserId(auth.getCurrentUser().getUid());
        
        db.collection(COLLECTION_BOOKMARKS)
                .add(bookmark)
                .addOnSuccessListener(documentReference -> 
                    Log.d(TAG, "Bookmark added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> 
                    Log.e(TAG, "Error adding bookmark", e));
    }
    
    public void delete(Bookmark bookmark) {
        if (auth.getCurrentUser() == null || bookmark.getDocumentId() == null) {
            return;
        }
        
        db.collection(COLLECTION_BOOKMARKS)
                .document(bookmark.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> 
                    Log.d(TAG, "Bookmark successfully deleted"))
                .addOnFailureListener(e -> 
                    Log.e(TAG, "Error deleting bookmark", e));
    }
    
    public void deleteById(int id, String type) {
        if (auth.getCurrentUser() == null) {
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        
        db.collection(COLLECTION_BOOKMARKS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("id", id)
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> 
                    Log.e(TAG, "Error finding bookmark to delete", e));
    }
    
    public boolean isBookmarked(int id, String type) {
        if (auth.getCurrentUser() == null) {
            return false;
        }
        
        String userId = auth.getCurrentUser().getUid();
        
        try {
            List<DocumentSnapshot> documents = db.collection(COLLECTION_BOOKMARKS)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("id", id)
                    .whereEqualTo("type", type)
                    .get()
                    .getResult()
                    .getDocuments();
            
            return !documents.isEmpty();
        } catch (Exception e) {
            Log.e(TAG, "Error checking if bookmark exists", e);
            return false;
        }
    }
    
    public void checkIfBookmarked(int id, String type, BookmarkCheckCallback callback) {
        if (auth.getCurrentUser() == null) {
            callback.onResult(false);
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        
        db.collection(COLLECTION_BOOKMARKS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("id", id)
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    callback.onResult(!queryDocumentSnapshots.isEmpty());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking if bookmarked", e);
                    callback.onResult(false);
                });
    }
    
    public interface BookmarkCheckCallback {
        void onResult(boolean isBookmarked);
    }
    
    /**
     * Get the total count of bookmarks for the current user.
     * This is a synchronous method that returns a default value for UI display.
     * In a real app, you would use a callback or LiveData to get the actual count.
     * 
     * @return The total number of bookmarks for the current user
     */
    public int getBookmarkCount() {
        // Get the current values from our LiveData
        List<Bookmark> currentBookmarks = allBookmarks.getValue();
        
        if (currentBookmarks != null) {
            return currentBookmarks.size();
        }
        
        // Default to 0 if we don't have any data yet
        // In a real app, you would load this data from Firestore instead
        return 0;
    }
} 