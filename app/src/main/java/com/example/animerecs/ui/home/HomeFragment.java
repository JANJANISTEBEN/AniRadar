package com.example.animerecs.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animerecs.R;
import com.example.animerecs.api.model.AnimeData;
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.data.repository.BookmarkRepository;
import com.example.animerecs.databinding.FragmentHomeBinding;
import com.example.animerecs.ui.anime.AnimeDetailActivity;
import com.example.animerecs.ui.anime.AnimeViewModel;
import com.example.animerecs.ui.bookmarks.BookmarksViewModel;
import com.example.animerecs.ui.manga.MangaDetailActivity;
import com.example.animerecs.ui.manga.MangaViewModel;
import com.example.animerecs.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecommendationAdapter.OnRecommendationClickListener {

    private FragmentHomeBinding binding;
    private AnimeViewModel animeViewModel;
    private MangaViewModel mangaViewModel;
    private BookmarksViewModel bookmarksViewModel;
    private RecommendationAdapter animeAdapter;
    private RecommendationAdapter mangaAdapter;
    private RecyclerView animeRecyclerView;
    private RecyclerView mangaRecyclerView;
    private ProgressBar animeLoadingIndicator;
    private ProgressBar mangaLoadingIndicator;
    private TextView noAnimeText;
    private TextView noMangaText;
    private Button viewBookmarksButton;
    private View animeErrorLayout;
    private View mangaErrorLayout;
    private TextView animeErrorText;
    private TextView mangaErrorText;
    private Button retryAnimeButton;
    private Button retryMangaButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        // Initialize ViewModels
        animeViewModel = new ViewModelProvider(this).get(AnimeViewModel.class);
        mangaViewModel = new ViewModelProvider(this).get(MangaViewModel.class);
        bookmarksViewModel = new ViewModelProvider(this).get(BookmarksViewModel.class);
        
        // Find views
        animeRecyclerView = binding.recyclerTopAnime;
        mangaRecyclerView = binding.recyclerTopManga;
        animeLoadingIndicator = binding.loadingTopAnime;
        mangaLoadingIndicator = binding.loadingTopManga;
        noAnimeText = binding.textNoAnime;
        noMangaText = binding.textNoManga;
        viewBookmarksButton = binding.btnViewBookmarks;
        animeErrorLayout = binding.animeErrorLayout;
        mangaErrorLayout = binding.mangaErrorLayout;
        animeErrorText = binding.animeErrorText;
        mangaErrorText = binding.mangaErrorText;
        retryAnimeButton = binding.retryAnimeButton;
        retryMangaButton = binding.retryMangaButton;
        
        // Apply theme colors to all views
        applyThemeColors();
        
        // Set up anime recommendations
        setupAnimeRecommendations();
        
        // Set up manga recommendations
        setupMangaRecommendations();
        
        // Set up bookmarks navigation
        viewBookmarksButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_bookmarks);
        });
        
        // Set up retry buttons
        retryAnimeButton.setOnClickListener(v -> {
            animeErrorLayout.setVisibility(View.GONE);
            animeLoadingIndicator.setVisibility(View.VISIBLE);
            animeViewModel.loadTopAnime();
        });
        
        retryMangaButton.setOnClickListener(v -> {
            mangaErrorLayout.setVisibility(View.GONE);
            mangaLoadingIndicator.setVisibility(View.VISIBLE);
            mangaViewModel.loadTopManga();
        });
        
        return root;
    }
    
    /**
     * Apply appropriate colors to views based on the current theme
     */
    private void applyThemeColors() {
        // Apply theme colors to all views using ColorUtils
        ColorUtils.applyThemeColors(binding.getRoot(), requireContext());
    }
    
    private void setupAnimeRecommendations() {
        // Set up RecyclerView
        animeRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        animeAdapter = new RecommendationAdapter(getContext(), new ArrayList<>(), 0, this);
        animeRecyclerView.setAdapter(animeAdapter);
        
        // Observe anime data
        animeViewModel.getAnimeList().observe(getViewLifecycleOwner(), animeList -> {
            if (animeList != null && !animeList.isEmpty()) {
                animeAdapter.updateItems(animeList);
                animeRecyclerView.setVisibility(View.VISIBLE);
                noAnimeText.setVisibility(View.GONE);
                animeErrorLayout.setVisibility(View.GONE);
            } else {
                animeRecyclerView.setVisibility(View.GONE);
                noAnimeText.setVisibility(View.VISIBLE);
            }
            animeLoadingIndicator.setVisibility(View.GONE);
        });
        
        // Observe errors
        animeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                animeLoadingIndicator.setVisibility(View.GONE);
                if (animeAdapter.getItemCount() == 0) {
                    animeRecyclerView.setVisibility(View.GONE);
                    noAnimeText.setVisibility(View.GONE);
                    animeErrorLayout.setVisibility(View.VISIBLE);
                    animeErrorText.setText(errorMessage);
                }
            }
        });
        
        // Load top anime
        animeViewModel.loadTopAnime();
    }
    
    private void setupMangaRecommendations() {
        // Set up RecyclerView
        mangaRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mangaAdapter = new RecommendationAdapter(getContext(), new ArrayList<>(), 1, this);
        mangaRecyclerView.setAdapter(mangaAdapter);
        
        // Observe manga data
        mangaViewModel.getMangaList().observe(getViewLifecycleOwner(), mangaList -> {
            if (mangaList != null && !mangaList.isEmpty()) {
                mangaAdapter.updateItems(mangaList);
                mangaRecyclerView.setVisibility(View.VISIBLE);
                noMangaText.setVisibility(View.GONE);
                mangaErrorLayout.setVisibility(View.GONE);
            } else {
                mangaRecyclerView.setVisibility(View.GONE);
                noMangaText.setVisibility(View.VISIBLE);
            }
            mangaLoadingIndicator.setVisibility(View.GONE);
        });
        
        // Observe errors
        mangaViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                mangaLoadingIndicator.setVisibility(View.GONE);
                if (mangaAdapter.getItemCount() == 0) {
                    mangaRecyclerView.setVisibility(View.GONE);
                    noMangaText.setVisibility(View.GONE);
                    mangaErrorLayout.setVisibility(View.VISIBLE);
                    mangaErrorText.setText(errorMessage);
                }
            }
        });
        
        // Load top manga
        mangaViewModel.loadTopManga();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    @Override
    public void onItemClick(Object item) {
        // Handle item click based on type
        if (item instanceof AnimeData) {
            AnimeData anime = (AnimeData) item;
            startActivity(AnimeDetailActivity.newIntent(getContext(), anime.getMalId()));
        } else if (item instanceof MangaData) {
            MangaData manga = (MangaData) item;
            startActivity(MangaDetailActivity.newIntent(getContext(), manga.getMalId()));
        }
    }
    
    @Override
    public void onBookmarkClick(Object item, boolean isCurrentlyBookmarked) {
        // Handle bookmark click based on type
        if (item instanceof AnimeData) {
            handleAnimeBookmarkClick((AnimeData) item, isCurrentlyBookmarked);
        } else if (item instanceof MangaData) {
            handleMangaBookmarkClick((MangaData) item, isCurrentlyBookmarked);
        }
    }
    
    private void handleAnimeBookmarkClick(AnimeData anime, boolean isCurrentlyBookmarked) {
        BookmarkRepository bookmarkRepository = new BookmarkRepository();
        
        if (isCurrentlyBookmarked) {
            // Remove from bookmarks
            bookmarkRepository.removeFromBookmarks(anime.getMalId(), Bookmark.TYPE_ANIME);
            Toast.makeText(getContext(), getString(R.string.bookmark_removed), Toast.LENGTH_SHORT).show();
        } else {
            // Add to bookmarks
            Bookmark bookmark = new Bookmark();
            bookmark.setItemId(anime.getMalId());
            bookmark.setTitle(anime.getTitle());
            bookmark.setImageUrl(anime.getImages().getJpg().getLargeImageUrl());
            bookmark.setType(Bookmark.TYPE_ANIME);
            bookmarkRepository.addToBookmarks(bookmark);
            Toast.makeText(getContext(), getString(R.string.bookmark_added), Toast.LENGTH_SHORT).show();
        }
        
        // Refresh bookmarks
        bookmarksViewModel.refreshBookmarks();
    }
    
    private void handleMangaBookmarkClick(MangaData manga, boolean isCurrentlyBookmarked) {
        BookmarkRepository bookmarkRepository = new BookmarkRepository();
        
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
        
        // Refresh bookmarks
        bookmarksViewModel.refreshBookmarks();
    }
} 