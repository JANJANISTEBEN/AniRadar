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

            // Handle error text visibility
            if (animeList.isEmpty()) {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText("No results found");
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
        viewModel.loadTopAnime();
        
        return root;
    }
    
    private void performSearch(String query) {
        if (query.isEmpty()) {
            // If search is cleared, load top anime
            isSearchActive = false;
            currentSearchQuery = "";
            viewModel.loadTopAnime();
        } else {
            // Perform search
            isSearchActive = true;
            currentSearchQuery = query;
            viewModel.searchAnime(query);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    @Override
    public void onAnimeClick(AnimeData anime) {
        // Navigate to detail screen
        if (getContext() != null && anime != null) {
            startActivity(AnimeDetailActivity.newIntent(getContext(), String.valueOf(anime.getId())));
        }
    }
    
    @Override
    public void onBookmarkClick(AnimeData anime, boolean isCurrentlyBookmarked) {
        if (isCurrentlyBookmarked) {
            // Remove bookmark
            bookmarkRepository.deleteById(anime.getId(), "anime");
            Toast.makeText(getContext(), "Removed from bookmarks", Toast.LENGTH_SHORT).show();
        } else {
            // Add bookmark
            Bookmark bookmark = new Bookmark(
                    anime.getId(),
                    anime.getTitle(),
                    anime.getImageUrl(),
                    "anime",
                    (float) anime.getScore(),
                    anime.getSynopsis(),
                    anime.getStatus(),
                    anime.getUrl(),
                    null // userId will be set in repository
            );
            
            bookmarksViewModel.insert(bookmark);
            Toast.makeText(getContext(), "Added to bookmarks", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.d("AnimeFragment", "onResume called");
        
        // Simplified onResume
        if (viewModel != null && recyclerView != null) {
            // Just refresh the current data
            viewModel.refreshCurrentData();
        }
    }
    
    @Override
    public void onPause() {
        android.util.Log.d("AnimeFragment", "onPause called");
        super.onPause();
    }

    /**
     * Apply appropriate colors to views based on the current theme
     */
    private void applyThemeColors() {
        // Apply theme colors to all views
        ColorUtils.applyThemeColors(binding.getRoot(), requireContext());
    }
} 