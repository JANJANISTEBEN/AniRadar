package com.example.animerecs.repository;

import android.util.Log;

import com.example.animerecs.api.model.AnimeData;
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.model.UserPreference;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class to manage user data, bookmarks, and preferences
 */
public class UserRepository {
    
    private static final String TAG = "UserRepository";
    
    // Collection paths
    private static final String USERS_COLLECTION = "users";
    private static final String BOOKMARKS_COLLECTION = "bookmarks";
    private static final String PREFERENCES_COLLECTION = "preferences";
    
    // Singleton instance
    private static UserRepository instance;
    
    // Firebase instances
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    
    /**
     * Get the singleton instance of the UserRepository
     */
    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private UserRepository() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }
    
    /**
     * Check if a user is currently logged in
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
    
    /**
     * Get the current Firebase user
     *
     * @return The currently logged in Firebase user, or null if not logged in
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
    
    /**
     * Get the ID of the current user
     *
     * @return The current user's ID, or null if not logged in
     */
    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    /**
     * Get the user's bookmarks collection reference
     *
     * @return The Firestore reference to the user's bookmarks collection
     */
    private CollectionReference getBookmarksCollection() {
        return firestore.collection(BOOKMARKS_COLLECTION);
    }
    
    /**
     * Get the user's preferences collection reference
     *
     * @return The Firestore reference to the user's preferences collection
     */
    private CollectionReference getPreferencesCollection() {
        return firestore.collection(PREFERENCES_COLLECTION);
    }
    
    /**
     * Save an anime bookmark
     *
     * @param animeData The anime data to bookmark
     * @return A task representing the operation
     */
    public Task<DocumentReference> saveAnimeBookmark(AnimeData animeData) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        Bookmark bookmark = new Bookmark(
            userId, 
            String.valueOf(animeData.getId()),
            Bookmark.TYPE_ANIME,
            animeData.getTitle(),
            animeData.getImageUrl()
        );
        return getBookmarksCollection().add(bookmark.toMap());
    }
    
    /**
     * Save a manga bookmark
     *
     * @param mangaData The manga data to bookmark
     * @return A task representing the operation
     */
    public Task<DocumentReference> saveMangaBookmark(MangaData mangaData) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        Bookmark bookmark = new Bookmark(
            userId, 
            String.valueOf(mangaData.getId()),
            Bookmark.TYPE_MANGA,
            mangaData.getTitle(),
            mangaData.getImageUrl()
        );
        return getBookmarksCollection().add(bookmark.toMap());
    }
    
    /**
     * Remove a bookmark by its document ID
     *
     * @param bookmarkId The ID of the bookmark document to remove
     * @return A task representing the operation
     */
    public Task<Void> removeBookmark(String bookmarkId) {
        return getBookmarksCollection().document(bookmarkId).delete();
    }
    
    /**
     * Get a bookmark by its item ID and type
     *
     * @param itemId The ID of the bookmarked item
     * @param itemType The type of the bookmarked item (anime or manga)
     * @return A task that resolves to the bookmark query
     */
    public Task<QuerySnapshot> getBookmarkByItemId(String itemId, String itemType) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        return getBookmarksCollection()
                .whereEqualTo("userId", userId)
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("type", itemType)
                .get();
    }
    
    /**
     * Check if an anime is bookmarked by the current user
     *
     * @param animeId The ID of the anime to check
     * @return A task that resolves to true if the anime is bookmarked, false otherwise
     */
    public Task<Boolean> isAnimeBookmarked(String animeId) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        
        getBookmarkByItemId(animeId, Bookmark.TYPE_ANIME)
                .addOnSuccessListener(querySnapshot -> {
                    boolean isBookmarked = !querySnapshot.isEmpty();
                    taskCompletionSource.setResult(isBookmarked);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking if anime is bookmarked", e);
                    taskCompletionSource.setResult(false);
                });
        
        return taskCompletionSource.getTask();
    }
    
    /**
     * Check if an anime is bookmarked by the current user
     *
     * @param animeId The ID of the anime to check
     * @return A task that resolves to true if the anime is bookmarked, false otherwise
     */
    public Task<Boolean> isAnimeBookmarked(int animeId) {
        return isAnimeBookmarked(String.valueOf(animeId));
    }
    
    /**
     * Check if a manga is bookmarked by the current user
     *
     * @param mangaId The ID of the manga to check
     * @return A task that resolves to true if the manga is bookmarked, false otherwise
     */
    public Task<Boolean> isMangaBookmarked(String mangaId) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        
        getBookmarkByItemId(mangaId, Bookmark.TYPE_MANGA)
                .addOnSuccessListener(querySnapshot -> {
                    boolean isBookmarked = !querySnapshot.isEmpty();
                    taskCompletionSource.setResult(isBookmarked);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking if manga is bookmarked", e);
                    taskCompletionSource.setResult(false);
                });
        
        return taskCompletionSource.getTask();
    }
    
    /**
     * Check if a manga is bookmarked by the current user
     *
     * @param mangaId The ID of the manga to check
     * @return A task that resolves to true if the manga is bookmarked, false otherwise
     */
    public Task<Boolean> isMangaBookmarked(int mangaId) {
        return isMangaBookmarked(String.valueOf(mangaId));
    }
    
    /**
     * Get all bookmarks for the current user
     *
     * @return A task that resolves to a list of bookmarks
     */
    public Task<List<Bookmark>> getUserBookmarks() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        return getBookmarksCollection()
                .whereEqualTo("userId", userId)
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    
                    List<Bookmark> bookmarks = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Bookmark bookmark = document.toObject(Bookmark.class);
                        if (bookmark != null) {
                            bookmark.setDocumentId(document.getId());
                            bookmarks.add(bookmark);
                        }
                    }
                    return bookmarks;
                });
    }
    
    /**
     * Get anime bookmarks for the current user
     *
     * @return A task that resolves to a list of anime bookmarks
     */
    public Task<List<Bookmark>> getUserAnimeBookmarks() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        return getBookmarksCollection()
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", Bookmark.TYPE_ANIME)
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    
                    List<Bookmark> bookmarks = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Bookmark bookmark = document.toObject(Bookmark.class);
                        if (bookmark != null) {
                            bookmark.setDocumentId(document.getId());
                            bookmarks.add(bookmark);
                        }
                    }
                    return bookmarks;
                });
    }
    
    /**
     * Get manga bookmarks for the current user
     *
     * @return A task that resolves to a list of manga bookmarks
     */
    public Task<List<Bookmark>> getUserMangaBookmarks() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        return getBookmarksCollection()
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", Bookmark.TYPE_MANGA)
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    
                    List<Bookmark> bookmarks = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Bookmark bookmark = document.toObject(Bookmark.class);
                        if (bookmark != null) {
                            bookmark.setDocumentId(document.getId());
                            bookmarks.add(bookmark);
                        }
                    }
                    return bookmarks;
                });
    }
    
    /**
     * Save a user preference for an item (anime or manga)
     *
     * @param itemId The ID of the item
     * @param itemType The type of the item (anime or manga)
     * @param status The status to save (like, dislike, watched, etc.)
     * @return A task representing the operation
     */
    public Task<DocumentReference> saveUserPreference(String itemId, String itemType, String status) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        TaskCompletionSource<DocumentReference> taskCompletionSource = new TaskCompletionSource<>();
        
        // First, get the existing preference document (if any)
        getPreferencesCollection()
                .whereEqualTo("userId", userId)
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("itemType", itemType)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Boolean> statusMap = new HashMap<>();
                    statusMap.put(status, true);
                    
                    if (querySnapshot.isEmpty()) {
                        // Create a new preference document
                        UserPreference preference = new UserPreference();
                        preference.setUserId(userId);
                        preference.setItemId(itemId);
                        preference.setItemType(itemType);
                        preference.setStatus(statusMap);
                        
                        getPreferencesCollection()
                                .add(preference.toMap())
                                .addOnSuccessListener(taskCompletionSource::setResult)
                                .addOnFailureListener(taskCompletionSource::setException);
                    } else {
                        // Update the existing preference document
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        DocumentReference docRef = document.getReference();
                        
                        UserPreference preference = document.toObject(UserPreference.class);
                        if (preference != null) {
                            Map<String, Boolean> currentStatus = preference.getStatus();
                            if (currentStatus == null) {
                                currentStatus = new HashMap<>();
                            }
                            
                            // Special handling for mutually exclusive statuses
                            if (status.equals(UserPreference.STATUS_LIKE)) {
                                currentStatus.remove(UserPreference.STATUS_DISLIKE);
                            } else if (status.equals(UserPreference.STATUS_DISLIKE)) {
                                currentStatus.remove(UserPreference.STATUS_LIKE);
                            } else if (status.equals(UserPreference.STATUS_WATCHED)) {
                                currentStatus.remove(UserPreference.STATUS_WATCH_LATER);
                            } else if (status.equals(UserPreference.STATUS_WATCH_LATER)) {
                                currentStatus.remove(UserPreference.STATUS_WATCHED);
                            } else if (status.equals(UserPreference.STATUS_READ)) {
                                currentStatus.remove(UserPreference.STATUS_READ_LATER);
                            } else if (status.equals(UserPreference.STATUS_READ_LATER)) {
                                currentStatus.remove(UserPreference.STATUS_READ);
                            }
                            
                            currentStatus.put(status, true);
                            
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("status", currentStatus);
                            updates.put("updatedAt", preference.getUpdatedAt());
                            
                            docRef.update(updates)
                                    .addOnSuccessListener(aVoid -> taskCompletionSource.setResult(docRef))
                                    .addOnFailureListener(taskCompletionSource::setException);
                        } else {
                            taskCompletionSource.setException(new Exception("Failed to parse preference document"));
                        }
                    }
                })
                .addOnFailureListener(taskCompletionSource::setException);
        
        return taskCompletionSource.getTask();
    }
    
    /**
     * Remove a user preference for an item (anime or manga)
     *
     * @param itemId The ID of the item
     * @param itemType The type of the item (anime or manga)
     * @return A task representing the operation
     */
    public Task<Void> removeUserPreference(String itemId, String itemType) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        
        getPreferencesCollection()
                .whereEqualTo("userId", userId)
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("itemType", itemType)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> taskCompletionSource.setResult(null))
                                .addOnFailureListener(taskCompletionSource::setException);
                    } else {
                        taskCompletionSource.setResult(null);
                    }
                })
                .addOnFailureListener(taskCompletionSource::setException);
        
        return taskCompletionSource.getTask();
    }
    
    /**
     * Get a user preference for an item (anime or manga)
     *
     * @param itemId The ID of the item
     * @param itemType The type of the item (anime or manga)
     * @return A task that resolves to the user preference, or null if not found
     */
    public Task<UserPreference> getUserItemPreference(String itemId, String itemType) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }
        
        TaskCompletionSource<UserPreference> taskCompletionSource = new TaskCompletionSource<>();
        
        getPreferencesCollection()
                .whereEqualTo("userId", userId)
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("itemType", itemType)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        UserPreference preference = document.toObject(UserPreference.class);
                        if (preference != null) {
                            preference.setId(document.getId());
                            taskCompletionSource.setResult(preference);
                        } else {
                            taskCompletionSource.setResult(null);
                        }
                    } else {
                        taskCompletionSource.setResult(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user preference", e);
                    taskCompletionSource.setException(e);
                });
        
        return taskCompletionSource.getTask();
    }
    
    /**
     * Save a user preference for an anime item
     *
     * @param animeId The ID of the anime
     * @param status The status to save (like, dislike, watched, etc.)
     * @return A task representing the operation
     */
    public Task<DocumentReference> saveAnimePreference(int animeId, String status) {
        return saveUserPreference(String.valueOf(animeId), Bookmark.TYPE_ANIME, status);
    }
    
    /**
     * Save a user preference for a manga item
     *
     * @param mangaId The ID of the manga
     * @param status The status to save (like, dislike, read, etc.)
     * @return A task representing the operation
     */
    public Task<DocumentReference> saveMangaPreference(int mangaId, String status) {
        return saveUserPreference(String.valueOf(mangaId), Bookmark.TYPE_MANGA, status);
    }
    
    /**
     * Get a user preference for an anime item
     *
     * @param animeId The ID of the anime
     * @return A task that resolves to the user preference, or null if not found
     */
    public Task<UserPreference> getAnimePreference(int animeId) {
        return getUserItemPreference(String.valueOf(animeId), Bookmark.TYPE_ANIME);
    }
    
    /**
     * Get a user preference for a manga item
     *
     * @param mangaId The ID of the manga
     * @return A task that resolves to the user preference, or null if not found
     */
    public Task<UserPreference> getMangaPreference(int mangaId) {
        return getUserItemPreference(String.valueOf(mangaId), Bookmark.TYPE_MANGA);
    }
} 