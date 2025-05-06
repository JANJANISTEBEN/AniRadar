package com.example.animerecs.ui.manga;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animerecs.R;
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.data.repository.BookmarkRepository;
import com.example.animerecs.databinding.FragmentMangaBinding;
import com.example.animerecs.ui.bookmarks.BookmarksViewModel;
import com.example.animerecs.ui.detail.MangaDetailActivity;
import com.example.animerecs.ui.filter.FilterDialog;
import com.example.animerecs.ui.filter.FilterOptions;
import com.example.animerecs.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;

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
    private Button retryButton;
    private ImageButton filterButton;
    private TextView activeFiltersText;
    private Button clearFiltersButton;
    
    private String currentSearchQuery = "";
    private boolean isSearchActive = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMangaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        // Apply theme colors
        applyThemeColors();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MangaViewModel.class);
        bookmarksViewModel = new ViewModelProvider(requireActivity()).get(BookmarksViewModel.class);
        bookmarkRepository = new BookmarkRepository();
        
        // Find views
        searchView = binding.searchView;
        recyclerView = binding.recyclerView;
        loadingIndicator = binding.progressBar;
        loadingMoreIndicator = binding.loadMoreProgressBar;
        errorTextView = binding.errorText;
        retryButton = binding.retryButton;
        filterButton = binding.filterButton;
        activeFiltersText = binding.activeFiltersText;
        clearFiltersButton = binding.clearFiltersButton;
        
        // Set up RecyclerView
        adapter = new MangaAdapter(getContext(), new ArrayList<>(), this);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Set up scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (dy > 0) { // Scrolling down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    
                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount - 5
                            && firstVisibleItem >= 0) {
                        viewModel.loadMore();
                    }
                }
            }
        });
        
        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    viewModel.searchManga(query);
                    searchView.clearFocus(); // Hide keyboard
                }
                return true;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && !searchView.isIconified()) {
                    // If search is cleared, return to top manga
                    viewModel.loadTopManga();
                }
                return true;
            }
        });
        
        // Set up filter button
        filterButton.setOnClickListener(v -> showFilterDialog());
        
        // Set up clear filters button
        clearFiltersButton.setOnClickListener(v -> viewModel.clearFilters());
        
        // Set up retry button
        retryButton.setOnClickListener(v -> viewModel.refreshCurrentData());
        
        // Observe manga list changes
        viewModel.getMangaList().observe(getViewLifecycleOwner(), this::onMangaDataReceived);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::onLoadingStateChanged);
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::onErrorReceived);
        viewModel.getActiveFilters().observe(getViewLifecycleOwner(), this::onFiltersChanged);
        
        // Load initial data
        if (viewModel.getMangaList().getValue() == null || viewModel.getMangaList().getValue().isEmpty()) {
            viewModel.loadTopManga();
        }
        
        return root;
    }
    
    /**
     * Apply appropriate colors to views based on the current theme
     */
    private void applyThemeColors() {
        // Apply theme colors to all views
        ColorUtils.applyThemeColors(binding.getRoot(), requireContext());
    }
    
    private void showFilterDialog() {
        FilterDialog dialog = new FilterDialog(getContext(), FilterDialog.CONTENT_TYPE_MANGA, options -> viewModel.setActiveFilters(options));
        dialog.setTitle(getString(R.string.filter_manga));
        
        // Set current filters if available
        if (viewModel.hasActiveFilters()) {
            dialog.setFilterOptions(viewModel.getActiveFilters().getValue());
        }
        
        dialog.show();
    }
    
    private void onMangaDataReceived(List<MangaData> mangaList) {
        adapter.setMangaList(mangaList);
        adapter.notifyDataSetChanged();
        
        if (mangaList.isEmpty() && viewModel.hasActiveFilters()) {
            errorTextView.setText(getString(R.string.no_results_found));
            errorTextView.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        } else {
            errorTextView.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
        }
    }
    
    private void onLoadingStateChanged(Boolean isLoading) {
        if (isLoading) {
            if (adapter.getItemCount() == 0) {
                // Initial loading
                loadingIndicator.setVisibility(View.VISIBLE);
                loadingMoreIndicator.setVisibility(View.GONE);
            } else {
                // Loading more
                loadingIndicator.setVisibility(View.GONE);
                loadingMoreIndicator.setVisibility(View.VISIBLE);
            }
        } else {
            // Loading finished
            loadingIndicator.setVisibility(View.GONE);
            loadingMoreIndicator.setVisibility(View.GONE);
        }
    }
    
    private void onErrorReceived(String errorMsg) {
        if (errorMsg != null && !errorMsg.isEmpty()) {
            if (adapter.getItemCount() == 0) {
                // Show error message only if we have no data
                errorTextView.setText(errorMsg);
                errorTextView.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
            }
        } else {
            errorTextView.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
        }
    }
    
    private void onFiltersChanged(FilterOptions filters) {
        if (filters != null && filters.hasActiveFilters()) {
            activeFiltersText.setVisibility(View.VISIBLE);
            clearFiltersButton.setVisibility(View.VISIBLE);
        } else {
            activeFiltersText.setVisibility(View.GONE);
            clearFiltersButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    @Override
    public void onMangaClick(MangaData manga) {
        Intent intent = new Intent(getContext(), MangaDetailActivity.class);
        intent.putExtra("manga_id", manga.getMalId());
        intent.putExtra("manga_title", manga.getTitle());
        intent.putExtra("manga_image_url", manga.getImages().getJpg().getLargeImageUrl());
        startActivity(intent);
    }
    
    @Override
    public void onBookmarkClick(MangaData manga, boolean isCurrentlyBookmarked) {
        if (isCurrentlyBookmarked) {
            // Remove from bookmarks
            bookmarkRepository.removeFromBookmarks(manga.getMalId(), Bookmark.TYPE_MANGA);
            Toast.makeText(getContext(), getString(R.string.bookmark_removed), Toast.LENGTH_SHORT).show();
        } else {
            // Add to bookmarks
            Bookmark bookmark = new Bookmark();
            bookmark.setItemId(manga.getMalId());
            bookmark.setTitle(manga.getTitle());
            bookmark.setImageUrl(manga.getImages().getJpg().getLargeImageUrl());
            bookmark.setType(Bookmark.TYPE_MANGA);
            bookmarkRepository.addToBookmarks(bookmark);
            Toast.makeText(getContext(), getString(R.string.bookmark_added), Toast.LENGTH_SHORT).show();
        }
        
        // Update bookmarks in ViewModel to reflect changes across the app
        bookmarksViewModel.refreshBookmarks();
        
        // Refresh adapter
        adapter.notifyDataSetChanged();
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