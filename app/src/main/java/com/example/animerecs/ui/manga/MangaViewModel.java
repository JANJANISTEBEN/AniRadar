package com.example.animerecs.ui.manga;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.api.ApiClient;
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.api.model.MangaListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaViewModel extends ViewModel {

    private final MutableLiveData<List<MangaData>> mangaList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    private int currentPage = 1;
    private boolean hasMorePages = true;
    private String currentQuery = null;

    public LiveData<List<MangaData>> getMangaList() {
        return mangaList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public void loadTopManga() {
        // Reset pagination if this is a new search
        currentPage = 1;
        hasMorePages = true;
        currentQuery = null;
        mangaList.setValue(new ArrayList<>());
        
        loadMoreTopManga();
    }
    
    public void loadMoreTopManga() {
        if (!hasMorePages || Boolean.TRUE.equals(isLoading.getValue())) {
            return;
        }
        
        isLoading.setValue(true);
        
        ApiClient.getApi().getTopManga(currentPage)
                .enqueue(new Callback<MangaListResponse>() {
                    @Override
                    public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MangaListResponse mangaResponse = response.body();
                            List<MangaData> currentList = mangaList.getValue();
                            List<MangaData> newList = mangaResponse.getData();
                            
                            if (currentList != null && !currentList.isEmpty()) {
                                List<MangaData> combinedList = new ArrayList<>(currentList);
                                combinedList.addAll(newList);
                                mangaList.setValue(combinedList);
                            } else {
                                mangaList.setValue(newList);
                            }
                            
                            // Check if there are more pages
                            if (newList.size() < 25) { // Jikan API returns 25 items per page
                                hasMorePages = false;
                            } else {
                                currentPage++;
                            }
                        } else {
                            errorMessage.setValue("Error loading manga: " + response.message());
                        }
                        isLoading.setValue(false);
                    }
                    
                    @Override
                    public void onFailure(Call<MangaListResponse> call, Throwable t) {
                        errorMessage.setValue("Network error: " + t.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }
    
    public void searchManga(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }
        
        // Reset pagination for new search
        currentPage = 1;
        hasMorePages = true;
        currentQuery = query;
        mangaList.setValue(new ArrayList<>());
        
        // Perform search
        loadMoreSearchResults(query);
    }
    
    public void loadMoreSearchResults(String query) {
        if (!hasMorePages || Boolean.TRUE.equals(isLoading.getValue()) || query == null || query.isEmpty()) {
            return;
        }
        
        isLoading.setValue(true);
        
        ApiClient.getApi().searchManga(query, currentPage)
                .enqueue(new Callback<MangaListResponse>() {
                    @Override
                    public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MangaListResponse mangaResponse = response.body();
                            List<MangaData> currentList = mangaList.getValue();
                            List<MangaData> newList = mangaResponse.getData();
                            
                            if (currentList != null && !currentList.isEmpty()) {
                                List<MangaData> combinedList = new ArrayList<>(currentList);
                                combinedList.addAll(newList);
                                mangaList.setValue(combinedList);
                            } else {
                                mangaList.setValue(newList);
                            }
                            
                            // Check if there are more pages
                            if (newList.size() < 25) {
                                hasMorePages = false;
                            } else {
                                currentPage++;
                            }
                        } else {
                            errorMessage.setValue("Error searching manga: " + response.message());
                        }
                        isLoading.setValue(false);
                    }
                    
                    @Override
                    public void onFailure(Call<MangaListResponse> call, Throwable t) {
                        errorMessage.setValue("Network error: " + t.getMessage());
                        isLoading.setValue(false);
                    }
                });
    }
    
    public void loadMore() {
        if (currentQuery != null && !currentQuery.isEmpty()) {
            loadMoreSearchResults(currentQuery);
        } else {
            loadMoreTopManga();
        }
    }
} 