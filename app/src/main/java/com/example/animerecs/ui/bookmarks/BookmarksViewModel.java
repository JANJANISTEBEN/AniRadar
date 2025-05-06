package com.example.animerecs.ui.bookmarks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.data.BookmarkRepository;
import com.example.animerecs.model.Bookmark;

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
        // Refresh data
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
    public void checkIfBookmarked(String id, String type, BookmarkRepository.BookmarkCheckCallback callback) {
        repository.checkIfBookmarked(id, type, callback);
    }
    
    public void checkIfBookmarked(String itemId, String type, Consumer<Boolean> callback) {
        repository.checkIfBookmarked(itemId, type, callback);
    }
} 