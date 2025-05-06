package com.example.animerecs.ui.bookmarks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.animerecs.R;
import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.databinding.FragmentBookmarksBinding;
import com.example.animerecs.ui.anime.AnimeDetailActivity;
import com.example.animerecs.ui.manga.MangaDetailActivity;
import com.example.animerecs.util.ColorUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class BookmarksFragment extends Fragment implements BookmarkAdapter.OnBookmarkClickListener {

    private FragmentBookmarksBinding binding;
    private BookmarksViewModel viewModel;
    private BookmarkAdapter animeAdapter;
    private BookmarkAdapter mangaAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(BookmarksViewModel.class);
        
        // Find views
        viewPager = binding.viewPager;
        tabLayout = binding.tabLayout;
        
        // Apply theme colors
        applyThemeColors();
        
        // Set up ViewPager with adapters
        BookmarksPagerAdapter pagerAdapter = new BookmarksPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Set up TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Anime");
                    break;
                case 1:
                    tab.setText("Manga");
                    break;
            }
        }).attach();
        
        return root;
    }
    
    /**
     * Apply appropriate colors to views based on the current theme
     */
    private void applyThemeColors() {
        // Apply theme colors to all views
        ColorUtils.applyThemeColors(binding.getRoot(), requireContext());
    }
    
    @Override
    public void onBookmarkClick(Bookmark bookmark) {
        if (bookmark.getType().equals("anime")) {
            startActivity(AnimeDetailActivity.newIntent(getContext(), String.valueOf(bookmark.getId())));
        } else if (bookmark.getType().equals("manga")) {
            startActivity(MangaDetailActivity.newIntent(getContext(), String.valueOf(bookmark.getId())));
        }
    }
    
    @Override
    public void onBookmarkRemove(Bookmark bookmark) {
        viewModel.delete(bookmark);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    // ViewPager adapter for Anime and Manga tabs
    private static class BookmarksPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        
        public BookmarksPagerAdapter(Fragment fragment) {
            super(fragment);
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new AnimeBookmarksFragment();
                case 1:
                    return new MangaBookmarksFragment();
                default:
                    return new AnimeBookmarksFragment();
            }
        }
        
        @Override
        public int getItemCount() {
            return 2; // Anime and Manga tabs
        }
    }
    
    // Fragment for Anime bookmarks
    public static class AnimeBookmarksFragment extends Fragment implements BookmarkAdapter.OnBookmarkClickListener {
        private RecyclerView recyclerView;
        private BookmarkAdapter adapter;
        private BookmarksViewModel viewModel;
        private TextView emptyView;
        private ProgressBar loadingIndicator;
        private View errorRetryLayout;
        private TextView errorRetryText;
        private Button retryButton;
        
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_bookmark_list, container, false);
            
            recyclerView = root.findViewById(R.id.recycler_bookmarks);
            emptyView = root.findViewById(R.id.text_empty);
            loadingIndicator = root.findViewById(R.id.loading_bookmarks);
            errorRetryLayout = root.findViewById(R.id.errorRetryLayout);
            errorRetryText = root.findViewById(R.id.errorRetryText);
            retryButton = root.findViewById(R.id.retryButton);
            
            // Apply theme colors
            applyThemeColors(root);
            
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new BookmarkAdapter(getContext(), new ArrayList<>(), this);
            recyclerView.setAdapter(adapter);
            
            viewModel = new ViewModelProvider(requireParentFragment()).get(BookmarksViewModel.class);
            
            retryButton.setOnClickListener(v -> {
                errorRetryLayout.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.VISIBLE);
                loadBookmarks();
            });
            
            loadBookmarks();
            
            return root;
        }
        
        /**
         * Apply appropriate colors to views based on the current theme
         */
        private void applyThemeColors(View rootView) {
            // Apply theme colors to all views
            ColorUtils.applyThemeColors(rootView, requireContext());
        }
        
        private void loadBookmarks() {
            loadingIndicator.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            errorRetryLayout.setVisibility(View.GONE);
            
            viewModel.getAnimeBookmarks().observe(getViewLifecycleOwner(), bookmarks -> {
                loadingIndicator.setVisibility(View.GONE);
                adapter.setBookmarks(bookmarks);
                
                if (bookmarks.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("No anime bookmarks yet");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            });
            
            // Observe error messages
            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    loadingIndicator.setVisibility(View.GONE);
                    if (adapter.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                        errorRetryLayout.setVisibility(View.VISIBLE);
                        errorRetryText.setText(errorMessage);
                    }
                }
            });
        }
        
        @Override
        public void onBookmarkClick(Bookmark bookmark) {
            if (getActivity() != null) {
                startActivity(AnimeDetailActivity.newIntent(getContext(), String.valueOf(bookmark.getId())));
            }
        }
        
        @Override
        public void onBookmarkRemove(Bookmark bookmark) {
            if (viewModel != null) {
                viewModel.delete(bookmark);
            }
        }
    }
    
    // Fragment for Manga bookmarks
    public static class MangaBookmarksFragment extends Fragment implements BookmarkAdapter.OnBookmarkClickListener {
        private RecyclerView recyclerView;
        private BookmarkAdapter adapter;
        private BookmarksViewModel viewModel;
        private TextView emptyView;
        private ProgressBar loadingIndicator;
        private View errorRetryLayout;
        private TextView errorRetryText;
        private Button retryButton;
        
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_bookmark_list, container, false);
            
            recyclerView = root.findViewById(R.id.recycler_bookmarks);
            emptyView = root.findViewById(R.id.text_empty);
            loadingIndicator = root.findViewById(R.id.loading_bookmarks);
            errorRetryLayout = root.findViewById(R.id.errorRetryLayout);
            errorRetryText = root.findViewById(R.id.errorRetryText);
            retryButton = root.findViewById(R.id.retryButton);
            
            // Apply theme colors
            applyThemeColors(root);
            
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new BookmarkAdapter(getContext(), new ArrayList<>(), this);
            recyclerView.setAdapter(adapter);
            
            viewModel = new ViewModelProvider(requireParentFragment()).get(BookmarksViewModel.class);
            
            retryButton.setOnClickListener(v -> {
                errorRetryLayout.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.VISIBLE);
                loadBookmarks();
            });
            
            loadBookmarks();
            
            return root;
        }
        
        /**
         * Apply appropriate colors to views based on the current theme
         */
        private void applyThemeColors(View rootView) {
            // Apply theme colors to all views
            ColorUtils.applyThemeColors(rootView, requireContext());
        }
        
        private void loadBookmarks() {
            loadingIndicator.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            errorRetryLayout.setVisibility(View.GONE);
            
            viewModel.getMangaBookmarks().observe(getViewLifecycleOwner(), bookmarks -> {
                loadingIndicator.setVisibility(View.GONE);
                adapter.setBookmarks(bookmarks);
                
                if (bookmarks.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("No manga bookmarks yet");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            });
            
            // Observe error messages
            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    loadingIndicator.setVisibility(View.GONE);
                    if (adapter.getItemCount() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                        errorRetryLayout.setVisibility(View.VISIBLE);
                        errorRetryText.setText(errorMessage);
                    }
                }
            });
        }
        
        @Override
        public void onBookmarkClick(Bookmark bookmark) {
            if (getActivity() != null) {
                startActivity(MangaDetailActivity.newIntent(getContext(), String.valueOf(bookmark.getId())));
            }
        }
        
        @Override
        public void onBookmarkRemove(Bookmark bookmark) {
            if (viewModel != null) {
                viewModel.delete(bookmark);
            }
        }
    }
} 