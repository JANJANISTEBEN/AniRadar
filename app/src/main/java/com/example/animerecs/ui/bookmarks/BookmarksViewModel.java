package com.example.animerecs.ui.bookmarks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.data.repository.BookmarkRepository;
import com.example.animerecs.data.model.Bookmark;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BookmarksViewModel extends ViewModel {

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isError = new MutableLiveData<>(false);
    private MutableLiveData<List<Bookmark>> animeBookmarks = new MutableLiveData<>();
    private MutableLiveData<List<Bookmark>> mangaBookmarks = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    private BookmarkRepository repository;
    
    public BookmarksViewModel() {
        repository = new BookmarkRepository();
        // Initialize by refreshing bookmarks
        refreshBookmarks();
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
        if (animeBookmarks.getValue() == null) {
            refreshBookmarks();
        }
        return animeBookmarks;
    }
    
    public LiveData<List<Bookmark>> getMangaBookmarks() {
        if (mangaBookmarks.getValue() == null) {
            refreshBookmarks();
        }
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
                        bookmark.setDocumentId(document.getId());
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
                        bookmark.setDocumentId(document.getId());
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
    
    public void addBookmark(Bookmark bookmark) {
        repository.addToBookmarks(bookmark);
        refreshBookmarks();
    }
    
    public void removeBookmark(Bookmark bookmark) {
        repository.removeFromBookmarks(bookmark.getItemId(), bookmark.getType());
        refreshBookmarks();
    }
    
    public void deleteBookmark(String id, String type) {
        repository.removeFromBookmarks(id, type);
        refreshBookmarks();
    }
    
    public void checkIfBookmarked(String id, String type, Consumer<Boolean> callback) {
        repository.checkIfBookmarked(id, type, callback);
    }
} 