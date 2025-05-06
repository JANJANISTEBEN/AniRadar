package com.example.animerecs.ui.bookmarks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.data.repository.BookmarkRepository;

import java.util.List;

public class BookmarksViewModel extends ViewModel {
    private BookmarkRepository repository;
    private LiveData<List<Bookmark>> allBookmarks;
    private LiveData<List<Bookmark>> animeBookmarks;
    private LiveData<List<Bookmark>> mangaBookmarks;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public BookmarksViewModel() {
        repository = new BookmarkRepository();
        allBookmarks = repository.getAllBookmarks();
        animeBookmarks = repository.getBookmarksByType("anime");
        mangaBookmarks = repository.getBookmarksByType("manga");
    }
    
    public LiveData<List<Bookmark>> getAllBookmarks() {
        return allBookmarks;
    }
    
    public LiveData<List<Bookmark>> getAnimeBookmarks() {
        return animeBookmarks;
    }
    
    public LiveData<List<Bookmark>> getMangaBookmarks() {
        return mangaBookmarks;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }
    
    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
    
    public void insert(Bookmark bookmark) {
        repository.insert(bookmark);
    }
    
    public void delete(Bookmark bookmark) {
        repository.delete(bookmark);
    }
    
    public void deleteById(int id, String type) {
        repository.deleteById(id, type);
    }
    
    public void checkIfBookmarked(int id, String type, BookmarkRepository.BookmarkCheckCallback callback) {
        repository.checkIfBookmarked(id, type, callback);
    }
} 