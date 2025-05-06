package com.example.animerecs.ui.anime;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.animerecs.api.model.AnimeData;
import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.data.repository.BookmarkRepository;
import com.example.animerecs.databinding.FragmentAnimeBinding;
import com.example.animerecs.ui.bookmarks.BookmarksViewModel;
import com.example.animerecs.ui.filter.FilterDialog;
import com.example.animerecs.ui.filter.FilterOptions;
import com.example.animerecs.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class AnimeFragment extends Fragment implements AnimeAdapter.OnAnimeClickListener {

    private FragmentAnimeBinding binding;
    private AnimeViewModel viewModel;
    private BookmarksViewModel bookmarksViewModel;
    private BookmarkRepository bookmarkRepository;
    private AnimeAdapter adapter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;
    private ProgressBar loadingMoreIndicator;
    private TextView errorTextView;
    private View errorRetryLayout;
    private Button retryButton;
    private TextView errorRetryText;
    private ImageButton filterButton;
    private TextView activeFiltersText;
    private Button clearFiltersButton;
    
    private String currentSearchQuery = "";
    private boolean isSearchActive = false;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        android.util.Log.d("AnimeFragment", "onAttach called");
    }
    
    @Override
    public void onDetach() {
        android.util.Log.d("AnimeFragment", "onDetach called");
        super.onDetach();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        // Apply theme colors
        applyThemeColors();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AnimeViewModel.class);
        bookmarksViewModel = new ViewModelProvider(this).get(BookmarksViewModel.class);
        bookmarkRepository = new BookmarkRepository();
        
        // Find views
        searchView = binding.searchView;
        recyclerView = binding.recyclerAnime;
        loadingIndicator = binding.loadingAnime;
        loadingMoreIndicator = binding.loadingMore;
        errorTextView = binding.textError;
        errorRetryLayout = binding.errorRetryLayout;
        retryButton = binding.retryButton;
        errorRetryText = binding.errorRetryText;
        filterButton = binding.filterButton;
        activeFiltersText = binding.activeFiltersText;
        clearFiltersButton = binding.clearFiltersButton;
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnimeAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        
        // Set up retry button
        retryButton.setOnClickListener(v -> {
            errorRetryLayout.setVisibility(View.GONE);
            if (isSearchActive && !currentSearchQuery.isEmpty()) {
                viewModel.searchAnime(currentSearchQuery);
            } else {
                viewModel.loadTopAnime();
            }
        });
        
        // Set up filter button
        filterButton.setOnClickListener(v -> showFilterDialog());
        
        // Set up clear filters button
        clearFiltersButton.setOnClickListener(v -> {
            viewModel.clearFilters();
            updateFilterIndicator(false);
        });
        
        // Set up scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !viewModel.getIsLoading().getValue()) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    // Load more when near the end of the list
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                            && firstVisibleItemPosition >= 0) {
                        viewModel.loadMore();
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
                    // User cleared the search
                    currentSearchQuery = "";
                    isSearchActive = false;
                    viewModel.loadTopAnime();
                }
                return true;
            }
        });
        
        // Observe anime list changes
        viewModel.getAnimeList().observe(getViewLifecycleOwner(), animeList -> {
            Log.d("AnimeFragment", "Received animeList update with " + animeList.size() + " items");
            if (!animeList.isEmpty()) {
                Log.d("AnimeFragment", "First anime: " + animeList.get(0).getTitle());
                Log.d("AnimeFragment", "First anime type: " + animeList.get(0).getType());
                Log.d("AnimeFragment", "First anime score: " + animeList.get(0).getScore());
                
                // Hide error views when we have data
                errorRetryLayout.setVisibility(View.GONE);
            } else if (viewModel.hasActiveFilters()) {
                // Show no results for filters message
                errorRetryText.setText(R.string.no_results_found);
                errorRetryLayout.setVisibility(View.VISIBLE);
            }
            
            // Create a new list to ensure the adapter sees it as new data
            List<AnimeData> newList = new ArrayList<>(animeList);
            
            // Only create a new adapter for search results
            if (isSearchActive) {
                Log.d("AnimeFragment", "Creating brand new adapter for search");
                
                // Create a completely new adapter instance
                adapter = null;
                adapter = new AnimeAdapter(requireContext(), newList, this);
                
                // Set adapter to null first to force garbage collection of previous adapter
                recyclerView.setAdapter(null);
                recyclerView.setAdapter(adapter);
                
                // Request a layout pass
                recyclerView.requestLayout();
            } else {
                // Update existing adapter for regular updates
                if (adapter == null) {
                    adapter = new AnimeAdapter(requireContext(), newList, this);
                    recyclerView.setAdapter(adapter);
                    Log.d("AnimeFragment", "New adapter created and set");
                } else {
                    // Use the enhanced forceRefresh method for more robust updates
                    adapter.forceRefresh(newList);
                    Log.d("AnimeFragment", "Existing adapter updated with forceRefresh");
                }
            }
            
            // Post UI refresh to ensure layout is complete
            recyclerView.post(() -> {
                Log.d("AnimeFragment", "Force UI refresh for RecyclerView");
                
                if (adapter != null) {
                    // Ensure adapter is notified of changes
                    adapter.notifyDataSetChanged();
                    
                    // Scroll to top when search is active
                    if (isSearchActive) {
                        Log.d("AnimeFragment", "Scrolling to top for search results");
                        recyclerView.scrollToPosition(0);
                    }
                }
            });
        });
        
        // Observe active filters
        viewModel.getActiveFilters().observe(getViewLifecycleOwner(), filterOptions -> {
            updateFilterIndicator(filterOptions != null && filterOptions.hasActiveFilters());
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingIndicator.setVisibility(isLoading && viewModel.getAnimeList().getValue().isEmpty() 
                    ? View.VISIBLE : View.GONE);
            loadingMoreIndicator.setVisibility(isLoading && !viewModel.getAnimeList().getValue().isEmpty() 
                    ? View.VISIBLE : View.GONE);
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                errorRetryText.setText(errorMessage);
                errorRetryLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Load initial data
        if (viewModel.getAnimeList().getValue().isEmpty()) {
            viewModel.loadTopAnime();
        }
        
        return root;
    }
    
    private void showFilterDialog() {
        if (getContext() == null) return;
        
        FilterDialog filterDialog = new FilterDialog(
                getContext(), 
                FilterDialog.CONTENT_TYPE_ANIME,
                options -> viewModel.setActiveFilters(options));
        
        // Set current filter options if any
        FilterOptions currentFilters = viewModel.getActiveFilters().getValue();
        if (currentFilters != null) {
            filterDialog.setFilterOptions(currentFilters);
        }
        
        filterDialog.show();
    }
    
    private void updateFilterIndicator(boolean isActive) {
        activeFiltersText.setVisibility(isActive ? View.VISIBLE : View.GONE);
        clearFiltersButton.setVisibility(isActive ? View.VISIBLE : View.GONE);
    }
    
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        
        currentSearchQuery = query.trim();
        isSearchActive = true;
        viewModel.searchAnime(currentSearchQuery);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    @Override
    public void onAnimeClick(AnimeData anime) {
        if (getContext() != null && anime != null) {
            // Launch the detail activity for this anime - use the same pattern as HomeFragment
            startActivity(AnimeDetailActivity.newIntent(getContext(), anime.getMalId()));
        }
    }
    
    @Override
    public void onBookmarkClick(AnimeData anime, boolean isCurrentlyBookmarked) {
        if (anime == null) return;
        
        if (isCurrentlyBookmarked) {
            // Remove from bookmarks
            bookmarkRepository.removeFromBookmarks(anime.getMalId(), Bookmark.TYPE_ANIME);
        } else {
            // Add to bookmarks
            Bookmark bookmark = new Bookmark();
            bookmark.setItemId(anime.getMalId());
            bookmark.setTitle(anime.getTitle());
            bookmark.setImageUrl(anime.getImages().getJpg().getImageUrl());
            bookmark.setType(Bookmark.TYPE_ANIME);
            bookmarkRepository.addToBookmarks(bookmark);
        }
        
        // Force a refresh of the adapter to show the new bookmark state
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Refresh bookmark states
        if (adapter != null) {
            adapter.refreshBookmarkStates();
        }
        
        // Check for pending filter change
        if (viewModel.hasActiveFilters()) {
            updateFilterIndicator(true);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
    
    private void applyThemeColors() {
        if (getContext() == null || binding == null) return;
        
        // Apply colors to all views
        ColorUtils.applyThemeColors(binding.getRoot(), requireContext());
    }
} 