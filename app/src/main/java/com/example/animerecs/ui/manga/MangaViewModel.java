package com.example.animerecs.ui.manga;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.api.ApiClient;
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.api.model.MangaListResponse;
import com.example.animerecs.ui.filter.FilterOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaViewModel extends ViewModel {

    private final MutableLiveData<List<MangaData>> mangaList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<FilterOptions> activeFilters = new MutableLiveData<>();
    
    private int currentPage = 1;
    private boolean hasMorePages = true;
    private String currentQuery = null;
    private boolean isFiltered = false;

    public LiveData<List<MangaData>> getMangaList() {
        return mangaList;
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
            searchManga(currentQuery);
        } else {
            loadTopManga();
        }
    }
    
    public void loadTopManga() {
        // Reset pagination
        currentPage = 1;
        hasMorePages = true;
        currentQuery = null;
        isFiltered = false;
        mangaList.setValue(new ArrayList<>());
        
        isLoading.setValue(true);
        
        ApiClient.getApi().getTopManga(currentPage, 25)
                .enqueue(new Callback<MangaListResponse>() {
                    @Override
                    public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<MangaData> mangaData = response.body().getMangaList();
                            if (mangaData != null) {
                                mangaList.setValue(mangaData);
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to load manga data");
                        }
                    }

                    @Override
                    public void onFailure(Call<MangaListResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    public void searchManga(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        
        // Reset pagination
        currentPage = 1;
        hasMorePages = true;
        currentQuery = query;
        isFiltered = false;
        mangaList.setValue(new ArrayList<>());
        
        isLoading.setValue(true);
        
        ApiClient.getApi().searchManga(query, currentPage, 25)
                .enqueue(new Callback<MangaListResponse>() {
                    @Override
                    public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<MangaData> mangaData = response.body().getMangaList();
                            if (mangaData != null) {
                                mangaList.setValue(mangaData);
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to search manga");
                        }
                    }

                    @Override
                    public void onFailure(Call<MangaListResponse> call, Throwable t) {
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
        mangaList.setValue(new ArrayList<>());
        
        loadMoreFilteredManga(options);
    }
    
    private void loadMoreFilteredManga(FilterOptions options) {
        if (!hasMorePages || isLoading.getValue()) {
            return;
        }
        
        isLoading.setValue(true);
        
        ApiClient.getApi().getMangaWithFilter(
                options.getType(),
                options.getStatus(),
                options.getMinScore(),
                options.getMaxScore(),
                options.getGenresAsString(),
                options.getOrderBy(),
                options.getSort(),
                currentPage,
                25)
                .enqueue(new Callback<MangaListResponse>() {
                    @Override
                    public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<MangaData> currentList = mangaList.getValue();
                            if (currentList == null) {
                                currentList = new ArrayList<>();
                            }
                            
                            List<MangaData> newMangaList = response.body().getMangaList();
                            if (newMangaList != null) {
                                currentList.addAll(newMangaList);
                                mangaList.setValue(currentList);
                                
                                // Update pagination info
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to apply filters");
                        }
                    }

                    @Override
                    public void onFailure(Call<MangaListResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    public void loadMore() {
        if (isFiltered) {
            loadMoreFilteredManga(activeFilters.getValue());
        } else if (currentQuery != null && !currentQuery.isEmpty()) {
            loadMoreSearchResults();
        } else {
            loadMoreTopManga();
        }
    }
    
    private void loadMoreTopManga() {
        if (!hasMorePages || isLoading.getValue()) {
            return;
        }
        
        isLoading.setValue(true);
        
        ApiClient.getApi().getTopManga(currentPage, 25)
                .enqueue(new Callback<MangaListResponse>() {
                    @Override
                    public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<MangaData> currentList = mangaList.getValue();
                            if (currentList == null) {
                                currentList = new ArrayList<>();
                            }
                            
                            List<MangaData> newMangaData = response.body().getMangaList();
                            if (newMangaData != null) {
                                currentList.addAll(newMangaData);
                                mangaList.setValue(currentList);
                                
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to load more manga");
                        }
                    }

                    @Override
                    public void onFailure(Call<MangaListResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    private void loadMoreSearchResults() {
        if (!hasMorePages || isLoading.getValue() || currentQuery == null || currentQuery.isEmpty()) {
            return;
        }
        
        isLoading.setValue(true);
        
        ApiClient.getApi().searchManga(currentQuery, currentPage, 25)
                .enqueue(new Callback<MangaListResponse>() {
                    @Override
                    public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            List<MangaData> currentList = mangaList.getValue();
                            if (currentList == null) {
                                currentList = new ArrayList<>();
                            }
                            
                            List<MangaData> newMangaData = response.body().getMangaList();
                            if (newMangaData != null) {
                                currentList.addAll(newMangaData);
                                mangaList.setValue(currentList);
                                
                                currentPage++;
                                hasMorePages = response.body().getPagination().hasNextPage();
                            }
                        } else {
                            errorMessage.setValue("Failed to load more search results");
                        }
                    }

                    @Override
                    public void onFailure(Call<MangaListResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }
    
    public void refreshCurrentData() {
        if (isFiltered) {
            applyFilters(activeFilters.getValue());
        } else if (currentQuery != null && !currentQuery.isEmpty()) {
            searchManga(currentQuery);
        } else {
            loadTopManga();
        }
    }
} 