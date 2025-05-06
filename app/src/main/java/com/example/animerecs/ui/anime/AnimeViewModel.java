package com.example.animerecs.ui.anime;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.api.ApiClient;
import com.example.animerecs.api.model.AnimeData;
import com.example.animerecs.api.model.AnimeListResponse;
import com.example.animerecs.ui.filter.FilterOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeViewModel extends ViewModel {

    private final MutableLiveData<List<AnimeData>> animeList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<FilterOptions> activeFilters = new MutableLiveData<>();
    
    private int currentPage = 1;
    private boolean hasMorePages = true;
    private String currentQuery = null;
    private boolean isFiltered = false;

    public LiveData<List<AnimeData>> getAnimeList() {
        return animeList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<FilterOptions> getActiveFilters() {
        return activeFilters;
    }
    
    public void setActiveFilters(FilterOptions filterOptions) {
        activeFilters.setValue(filterOptions);
        applyFilters(filterOptions);
    }
    
    public boolean hasActiveFilters() {
        FilterOptions filters = activeFilters.getValue();
        return filters != null && filters.hasActiveFilters();
    }
    
    public void clearFilters() {
        activeFilters.setValue(null);
        isFiltered = false;
        
        if (currentQuery != null && !currentQuery.isEmpty()) {
            searchAnime(currentQuery);
        } else {
            loadTopAnime();
        }
    }
    
    public void loadTopAnime() {
        // Reset pagination if this is a new search
        currentPage = 1;
        hasMorePages = true;
        currentQuery = null;
        isFiltered = false;
        animeList.setValue(new ArrayList<>());
        
        loadMoreTopAnime();
    }
    
    public void loadMoreTopAnime() {
        if (!hasMorePages || isLoading.getValue()) {
            return;
        }
        
        isLoading.setValue(true);
        
        ApiClient.getApi().getTopAnime(currentPage, 25, "")
                .enqueue(new Callback<AnimeListResponse>() {
                    @Override
                    public void onResponse(Call<AnimeListResponse> call, Response<AnimeListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<AnimeData> currentList = animeList.getValue();
                            if (currentList == null) {
                                currentList = new ArrayList<>();
                            }
                            
                            List<AnimeData> newAnimeData = response.body().getAnimeList();
                            if (newAnimeData != null) {
                                currentList.addAll(newAnimeData);
                                animeList.setValue(currentList);
                                
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to load more anime");
                        }
                    }

                    @Override
                    public void onFailure(Call<AnimeListResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    public void searchAnime(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        
        // Reset pagination for new search
        currentPage = 1;
        hasMorePages = true;
        currentQuery = query;
        isFiltered = false;
        animeList.setValue(new ArrayList<>());
        
        isLoading.setValue(true);
        
        ApiClient.getApi().searchAnime(query, currentPage, 20)
                .enqueue(new Callback<AnimeListResponse>() {
                    @Override
                    public void onResponse(Call<AnimeListResponse> call, Response<AnimeListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<AnimeData> newAnimeList = response.body().getAnimeList();
                            if (newAnimeList != null) {
                                animeList.setValue(newAnimeList);
                                
                                // Update pagination info
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to search anime");
                        }
                    }

                    @Override
                    public void onFailure(Call<AnimeListResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    public void applyFilters(FilterOptions options) {
        if (options == null || !options.hasActiveFilters()) {
            // If no filters, just reset to normal view
            clearFilters();
            return;
        }
        
        // Reset pagination
        currentPage = 1;
        hasMorePages = true;
        isFiltered = true;
        animeList.setValue(new ArrayList<>());
        
        loadMoreFilteredAnime(options);
    }
    
    private void loadMoreFilteredAnime(FilterOptions options) {
        if (!hasMorePages || isLoading.getValue()) {
            return;
        }
        
        isLoading.setValue(true);
        
        ApiClient.getApi().filterAnime(
                currentPage,
                20,
                options.getType(),
                options.getStatus(),
                options.getMinScore(),
                options.getMaxScore(),
                options.getGenresAsString(),
                options.getOrderBy(),
                options.getSort())
                .enqueue(new Callback<AnimeListResponse>() {
                    @Override
                    public void onResponse(Call<AnimeListResponse> call, Response<AnimeListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<AnimeData> currentList = animeList.getValue();
                            if (currentList == null) {
                                currentList = new ArrayList<>();
                            }
                            
                            List<AnimeData> newAnimeList = response.body().getAnimeList();
                            if (newAnimeList != null) {
                                currentList.addAll(newAnimeList);
                                animeList.setValue(currentList);
                                
                                // Update pagination info
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to apply filters");
                        }
                    }

                    @Override
                    public void onFailure(Call<AnimeListResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    public void loadMore() {
        if (isFiltered) {
            loadMoreFilteredAnime(activeFilters.getValue());
        } else if (currentQuery != null && !currentQuery.isEmpty()) {
            loadMoreSearchResults(currentQuery);
        } else {
            loadMoreTopAnime();
        }
    }
    
    public void loadMoreSearchResults(String query) {
        if (!hasMorePages || isLoading.getValue() || query == null || query.trim().isEmpty()) {
            return;
        }
        
        isLoading.setValue(true);
        
        ApiClient.getApi().searchAnime(query, currentPage, 20)
                .enqueue(new Callback<AnimeListResponse>() {
                    @Override
                    public void onResponse(Call<AnimeListResponse> call, Response<AnimeListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<AnimeData> currentList = animeList.getValue();
                            if (currentList == null) {
                                currentList = new ArrayList<>();
                            }
                            
                            List<AnimeData> newAnimeList = response.body().getAnimeList();
                            if (newAnimeList != null) {
                                currentList.addAll(newAnimeList);
                                animeList.setValue(currentList);
                                
                                // Update pagination info
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to load more search results");
                        }
                    }

                    @Override
                    public void onFailure(Call<AnimeListResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }

    public void refreshCurrentData() {
        android.util.Log.d("AnimeViewModel", "Refreshing current data");
        
        if (isFiltered) {
            android.util.Log.d("AnimeViewModel", "Refreshing filtered data");
            applyFilters(activeFilters.getValue());
        } else if (currentQuery != null && !currentQuery.isEmpty()) {
            // If we're searching, reload the search
            android.util.Log.d("AnimeViewModel", "Refreshing search with query: " + currentQuery);
            searchAnime(currentQuery);
        } else {
            // Otherwise reload top anime
            android.util.Log.d("AnimeViewModel", "Refreshing top anime");
            loadTopAnime();
        }
    }
    
    /**
     * Forces a refresh of the UI by posting the current data again.
     * This can help when data has changed but the UI hasn't updated.
     */
    public void forceUiRefresh() {
        List<AnimeData> currentData = animeList.getValue();
        
        if (currentData != null) {
            android.util.Log.d("AnimeViewModel", "Forcing UI refresh with " + currentData.size() + " items");
            
            // Create a completely new list instance to ensure LiveData detects a change
            List<AnimeData> refreshedList = new ArrayList<>();
            
            // Add each item individually to ensure it's a completely new instance
            for (AnimeData item : currentData) {
                refreshedList.add(item);
            }
            
            // Use setValue or postValue depending on thread
            if (android.os.Looper.getMainLooper().isCurrentThread()) {
                animeList.setValue(refreshedList);
                android.util.Log.d("AnimeViewModel", "Used setValue for UI refresh");
            } else {
                animeList.postValue(refreshedList);
                android.util.Log.d("AnimeViewModel", "Used postValue for UI refresh");
            }
        } else {
            android.util.Log.d("AnimeViewModel", "Cannot force UI refresh with null data");
        }
    }
} 