package com.example.animerecs.ui.manga;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animerecs.R;
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.data.repository.BookmarkRepository;
import com.example.animerecs.databinding.FragmentMangaBinding;
import com.example.animerecs.ui.bookmarks.BookmarksViewModel;
import com.example.animerecs.util.ColorUtils;

import java.util.ArrayList;

public class MangaFragment extends Fragment implements MangaAdapter.OnMangaClickListener {

    private FragmentMangaBinding binding;
    private MangaViewModel viewModel;
    private BookmarksViewModel bookmarksViewModel;
    private BookmarkRepository bookmarkRepository;
    private MangaAdapter adapter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;
    private ProgressBar loadingMoreIndicator;
    private TextView errorTextView;
    private View errorRetryLayout;
    private Button retryButton;
    private TextView errorRetryText;
    
    private String currentSearchQuery = "";
    private boolean isSearchActive = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMangaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        // Apply theme colors
        applyThemeColors();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MangaViewModel.class);
        bookmarksViewModel = new ViewModelProvider(this).get(BookmarksViewModel.class);
        bookmarkRepository = new BookmarkRepository();
        
        // Find views
        searchView = binding.searchView;
        recyclerView = binding.recyclerManga;
        loadingIndicator = binding.loadingManga;
        loadingMoreIndicator = binding.loadingMore;
        errorTextView = binding.textError;
        errorRetryLayout = binding.errorRetryLayout;
        retryButton = binding.retryButton;
        errorRetryText = binding.errorRetryText;
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MangaAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        
        // Set up retry button
        retryButton.setOnClickListener(v -> {
            errorRetryLayout.setVisibility(View.GONE);
            if (isSearchActive && !currentSearchQuery.isEmpty()) {
                viewModel.searchManga(currentSearchQuery);
            } else {
                viewModel.loadTopManga();
            }
        });
        
        // Set up scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    // Load more when near the end of the list
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                            && firstVisibleItemPosition >= 0) {
                        if (isSearchActive) {
                            viewModel.loadMoreSearchResults(currentSearchQuery);
                        } else {
                            viewModel.loadMoreTopManga();
                        }
                    }
                }
            }
        });
        
        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && !currentSearchQuery.isEmpty()) {
                    // When text is cleared, reset search
                    performSearch("");
                }
                return true;
            }
        });
        
        // Observe manga list changes
        viewModel.getMangaList().observe(getViewLifecycleOwner(), mangaList -> {
            android.util.Log.d("MangaFragment", "Received mangaList update with " + mangaList.size() + " items");
            adapter.setMangaList(mangaList);
            adapter.notifyDataSetChanged(); // Force refresh the entire list
            
            if (!mangaList.isEmpty()) {
                // Hide error views when we have data
                errorRetryLayout.setVisibility(View.GONE);
            }
            
            if (mangaList.isEmpty()) {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(R.string.no_results_found);
            } else {
                errorTextView.setVisibility(View.GONE);
            }
        });
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (adapter.getItemCount() == 0) {
                // Show main loading indicator for initial load
                loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            } else {
                // Show bottom loading indicator for pagination
                loadingMoreIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            
            // Ensure both indicators are hidden when not loading
            if (!isLoading) {
                loadingIndicator.setVisibility(View.GONE);
                loadingMoreIndicator.setVisibility(View.GONE);
            }
            
            // Hide error retry layout when loading
            if (isLoading) {
                errorRetryLayout.setVisibility(View.GONE);
            }
        });
        
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                
                if (adapter.getItemCount() == 0) {
                    // Show error retry layout instead of just text
                    errorTextView.setVisibility(View.GONE);
                    errorRetryLayout.setVisibility(View.VISIBLE);
                    errorRetryText.setText(errorMessage);
                }
            } else {
                // No error, hide retry layout
                errorRetryLayout.setVisibility(View.GONE);
            }
        });
        
        // Load initial data
        viewModel.loadTopManga();
        
        return root;
    }
    
    /**
     * Apply appropriate colors to views based on the current theme
     */
    private void applyThemeColors() {
        // Apply theme colors to all views
        ColorUtils.applyThemeColors(binding.getRoot(), requireContext());
    }
    
    private void performSearch(String query) {
        if (query.isEmpty()) {
            // If search is cleared, reset state
            isSearchActive = false;
            currentSearchQuery = "";
            
            // Load top manga
            viewModel.loadTopManga();
        } else {
            // Perform search
            isSearchActive = true;
            currentSearchQuery = query;
            viewModel.searchManga(query);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    @Override
    public void onMangaClick(MangaData manga) {
        // Navigate to detail screen
        if (getContext() != null && manga != null) {
            startActivity(MangaDetailActivity.newIntent(getContext(), String.valueOf(manga.getId())));
        }
    }
    
    @Override
    public void onBookmarkClick(MangaData manga, boolean isCurrentlyBookmarked) {
        if (isCurrentlyBookmarked) {
            // Remove bookmark
            bookmarkRepository.deleteById(manga.getId(), "manga");
            Toast.makeText(getContext(), "Removed from bookmarks", Toast.LENGTH_SHORT).show();
        } else {
            // Add bookmark
            Bookmark bookmark = new Bookmark(
                    manga.getId(),
                    manga.getTitle(),
                    manga.getImageUrl(),
                    "manga",
                    (float) manga.getScore(),
                    manga.getSynopsis(),
                    manga.getStatus(),
                    manga.getUrl(),
                    null // userId will be set in repository
            );
            
            bookmarksViewModel.insert(bookmark);
            Toast.makeText(getContext(), "Added to bookmarks", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Refresh adapter to update bookmark icons
        if (adapter != null && adapter.getItemCount() > 0) {
            adapter.notifyDataSetChanged();
        }
    }
} 