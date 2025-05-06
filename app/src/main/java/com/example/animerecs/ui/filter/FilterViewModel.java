package com.example.animerecs.ui.filter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel to manage filter operations across the app
 */
public class FilterViewModel extends ViewModel {
    
    // Content type constants
    public static final String CONTENT_TYPE_ANIME = "ANIME";
    public static final String CONTENT_TYPE_MANGA = "MANGA";
    
    // LiveData to store filter options for anime and manga
    private final MutableLiveData<FilterOptions> animeFilterOptions = new MutableLiveData<>();
    private final MutableLiveData<FilterOptions> mangaFilterOptions = new MutableLiveData<>();
    
    /**
     * Gets the current filter options for anime
     */
    public LiveData<FilterOptions> getAnimeFilterOptions() {
        return animeFilterOptions;
    }
    
    /**
     * Gets the current filter options for manga
     */
    public LiveData<FilterOptions> getMangaFilterOptions() {
        return mangaFilterOptions;
    }
    
    /**
     * Sets new filter options for anime
     */
    public void setAnimeFilterOptions(FilterOptions options) {
        animeFilterOptions.setValue(options);
    }
    
    /**
     * Sets new filter options for manga
     */
    public void setMangaFilterOptions(FilterOptions options) {
        mangaFilterOptions.setValue(options);
    }
    
    /**
     * Checks if anime has active filters
     */
    public boolean hasAnimeFilters() {
        FilterOptions options = animeFilterOptions.getValue();
        return options != null && options.hasActiveFilters();
    }
    
    /**
     * Checks if manga has active filters
     */
    public boolean hasMangaFilters() {
        FilterOptions options = mangaFilterOptions.getValue();
        return options != null && options.hasActiveFilters();
    }
    
    /**
     * Clears anime filters
     */
    public void clearAnimeFilters() {
        animeFilterOptions.setValue(null);
    }
    
    /**
     * Clears manga filters
     */
    public void clearMangaFilters() {
        mangaFilterOptions.setValue(null);
    }
    
    /**
     * Gets filter options based on content type
     */
    public FilterOptions getFilterOptions(String contentType) {
        if (CONTENT_TYPE_ANIME.equals(contentType)) {
            return animeFilterOptions.getValue();
        } else if (CONTENT_TYPE_MANGA.equals(contentType)) {
            return mangaFilterOptions.getValue();
        }
        return null;
    }
    
    /**
     * Sets filter options based on content type
     */
    public void setFilterOptions(String contentType, FilterOptions options) {
        if (CONTENT_TYPE_ANIME.equals(contentType)) {
            setAnimeFilterOptions(options);
        } else if (CONTENT_TYPE_MANGA.equals(contentType)) {
            setMangaFilterOptions(options);
        }
    }
} 