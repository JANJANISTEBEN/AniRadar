package com.example.animerecs.ui.bookmarks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.data.BookmarkRepository;
import com.example.animerecs.model.Bookmark;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BookmarksViewModel extends ViewModel {

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isError = new MutableLiveData<>(false);
    private MutableLiveData<List<Bookmark>> animeBookmarks = new MutableLiveData<>();
    private MutableLiveData<List<Bookmark>> mangaBookmarks = new MutableLiveData<>();
    private LiveData<List<Bookmark>> allBookmarks;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    private BookmarkRepository repository;
    
    public BookmarksViewModel() {
        repository = new BookmarkRepository();
        allBookmarks = repository.getAllBookmarks();
    }
    
    public LiveData<List<Bookmark>> getAllBookmarks() {
        return allBookmarks;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<Boolean> getIsError() {
        return isError;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<List<Bookmark>> getAnimeBookmarks() {
        return animeBookmarks;
    }
    
    public LiveData<List<Bookmark>> getMangaBookmarks() {
        return mangaBookmarks;
    }
    
    public void refreshBookmarks() {
        isLoading.setValue(true);
        isError.setValue(false);
        
        String userId = repository.getCurrentUserId();
        if (userId == null) {
            isLoading.setValue(false);
            errorMessage.setValue("Please log in to view bookmarks");
            return;
        }
        
        // Load anime bookmarks
        repository.getBookmarksCollection()
                .whereEqualTo("type", Bookmark.TYPE_ANIME)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Bookmark> bookmarks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Bookmark bookmark = document.toObject(Bookmark.class);
                        bookmarks.add(bookmark);
                    }
                    animeBookmarks.setValue(bookmarks);
                })
                .addOnFailureListener(e -> {
                    isError.setValue(true);
                    errorMessage.setValue("Failed to load anime bookmarks");
                });
        
        // Load manga bookmarks
        repository.getBookmarksCollection()
                .whereEqualTo("type", Bookmark.TYPE_MANGA)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Bookmark> bookmarks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Bookmark bookmark = document.toObject(Bookmark.class);
                        bookmarks.add(bookmark);
                    }
                    mangaBookmarks.setValue(bookmarks);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    isError.setValue(true);
                    errorMessage.setValue("Failed to load manga bookmarks");
                    isLoading.setValue(false);
                });
    }
    
    // Method to add bookmark
    public void addBookmark(Bookmark bookmark) {
        repository.insert(bookmark);
        refreshBookmarks();
    }
    
    // Method to remove bookmark
    public void removeBookmark(Bookmark bookmark) {
        repository.delete(bookmark);
        refreshBookmarks();
    }
    
    // Method to delete bookmark by ID and type
    public void deleteBookmark(String id, String type) {
        repository.removeFromBookmarks(id, type);
        refreshBookmarks();
    }
    
    // Method to check if item is bookmarked
    public void checkIfBookmarked(String id, String type, Consumer<Boolean>     callback) {
        repository.checkIfBookmarked(id, type, callback);
    }
} 