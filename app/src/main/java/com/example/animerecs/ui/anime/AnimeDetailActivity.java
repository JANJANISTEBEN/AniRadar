package com.example.animerecs.ui.anime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.animerecs.R;
import com.example.animerecs.api.ApiClient;
import com.example.animerecs.api.model.AnimeData;
import com.example.animerecs.api.model.AnimeResponse;
import com.example.animerecs.data.repository.ThemeManager;
import com.example.animerecs.model.Bookmark;
import com.example.animerecs.model.UserPreference;
import com.example.animerecs.repository.UserRepository;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeDetailActivity extends AppCompatActivity {

    private static final String EXTRA_ANIME_ID = "anime_id";
    
    private ImageView coverImageView;
    private TextView titleTextView, synopsisTextView, scoreTextView, 
                     typeTextView, episodesTextView, airedTextView, genresTextView;
    private Button bookmarkButton, recommendButton;
    private ChipGroup preferencesChipGroup;
    private Chip likeChip, dislikeChip, watchedChip, watchLaterChip;
    
    private UserRepository userRepository;
    
    private String animeId;
    private AnimeData animeData;
    private boolean isBookmarked = false;

    public static Intent newIntent(Context context, String animeId) {
        Intent intent = new Intent(context, AnimeDetailActivity.class);
        intent.putExtra(EXTRA_ANIME_ID, animeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize theme before setting the content view
        ThemeManager.getInstance(this).initializeTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);
        
        // Apply theme colors to all views
        com.example.animerecs.util.ColorUtils.applyThemeColors(findViewById(android.R.id.content), this);
        
        userRepository = UserRepository.getInstance();
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        coverImageView = findViewById(R.id.anime_cover);
        titleTextView = findViewById(R.id.anime_title);
        synopsisTextView = findViewById(R.id.anime_synopsis);
        scoreTextView = findViewById(R.id.anime_score);
        typeTextView = findViewById(R.id.anime_type);
        episodesTextView = findViewById(R.id.anime_episodes);
        airedTextView = findViewById(R.id.anime_aired);
        genresTextView = findViewById(R.id.anime_genres);
        
        bookmarkButton = findViewById(R.id.btn_bookmark);
        recommendButton = findViewById(R.id.btn_recommend_similar);
        
        preferencesChipGroup = findViewById(R.id.anime_preferences);
        
        likeChip = findViewById(R.id.chip_like);
        dislikeChip = findViewById(R.id.chip_dislike);
        watchedChip = findViewById(R.id.chip_watched);
        watchLaterChip = findViewById(R.id.chip_watch_later);
        
        FloatingActionButton fab = findViewById(R.id.fab_share);
        fab.setOnClickListener(this::shareAnime);
        
        if (getIntent().hasExtra(EXTRA_ANIME_ID)) {
            animeId = getIntent().getStringExtra(EXTRA_ANIME_ID);
            if (animeId != null) {
                loadAnimeDetails();
                checkIfBookmarked();
                loadUserPreferences();
            } else {
                showError("Anime ID not found");
                finish();
            }
        } else {
            showError("Anime ID not found");
            finish();
        }
        
        setupClickListeners();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Re-apply theme colors when activity resumes
        com.example.animerecs.util.ColorUtils.applyThemeColors(findViewById(android.R.id.content), this);
    }
    
    private void loadAnimeDetails() {
        ApiClient.getApi().getAnimeById(animeId).enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnimeResponse> call, @NonNull Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    animeData = response.body().getData();
                    displayAnimeDetails(animeData);
                } else {
                    showError("Failed to load anime details");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeResponse> call, @NonNull Throwable t) {
                showError("Network error: " + t.getMessage());
            }
        });
    }
    
    private void displayAnimeDetails(AnimeData anime) {
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(anime.getTitle());
        
        titleTextView.setText(anime.getTitle());
        
        if (anime.getImageUrl() != null && !anime.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(anime.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(coverImageView);
        }
        
        synopsisTextView.setText(anime.getSynopsis());
        scoreTextView.setText(String.format("Score: %.2f", anime.getScore()));
        typeTextView.setText(String.format("Type: %s", anime.getType()));
        episodesTextView.setText(String.format("Episodes: %d", anime.getEpisodes()));
        airedTextView.setText(String.format("Aired: %s", anime.getAiredString()));
        genresTextView.setText(String.format("Genres: %s", anime.getGenresString()));
    }
    
    private void loadUserPreferences() {
        if (!userRepository.isUserLoggedIn()) {
            preferencesChipGroup.setVisibility(View.GONE);
            return;
        }
        
        preferencesChipGroup.setVisibility(View.VISIBLE);
        
        userRepository.getUserItemPreference(animeId, Bookmark.TYPE_ANIME)
                .addOnSuccessListener(preference -> {
                    if (preference != null) {
                        likeChip.setChecked(preference.isLiked());
                        dislikeChip.setChecked(preference.isDisliked());
                        watchedChip.setChecked(preference.isWatched());
                        watchLaterChip.setChecked(preference.isWatchLater());
                    } else {
                        likeChip.setChecked(false);
                        dislikeChip.setChecked(false);
                        watchedChip.setChecked(false);
                        watchLaterChip.setChecked(false);
                    }
                });
    }
    
    private void setupClickListeners() {
        bookmarkButton.setOnClickListener(v -> toggleBookmark());
        
        recommendButton.setOnClickListener(v -> recommendSimilar());
        
        likeChip.setOnClickListener(v -> {
            if (likeChip.isChecked()) {
                savePreference(UserPreference.STATUS_LIKE);
                dislikeChip.setChecked(false);
            } else {
                removePreference(UserPreference.STATUS_LIKE);
            }
        });
        
        dislikeChip.setOnClickListener(v -> {
            if (dislikeChip.isChecked()) {
                savePreference(UserPreference.STATUS_DISLIKE);
                likeChip.setChecked(false);
            } else {
                removePreference(UserPreference.STATUS_DISLIKE);
            }
        });
        
        watchedChip.setOnClickListener(v -> {
            if (watchedChip.isChecked()) {
                savePreference(UserPreference.STATUS_WATCHED);
                watchLaterChip.setChecked(false);
            } else {
                removePreference(UserPreference.STATUS_WATCHED);
            }
        });
        
        watchLaterChip.setOnClickListener(v -> {
            if (watchLaterChip.isChecked()) {
                savePreference(UserPreference.STATUS_WATCH_LATER);
                watchedChip.setChecked(false);
            } else {
                removePreference(UserPreference.STATUS_WATCH_LATER);
            }
        });
    }
    
    private void toggleBookmark() {
        if (animeData == null) return;
        
        if (isBookmarked) {
            userRepository.getBookmarkByItemId(animeId, Bookmark.TYPE_ANIME)
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String bookmarkId = querySnapshot.getDocuments().get(0).getId();
                            userRepository.removeBookmark(bookmarkId)
                                    .addOnSuccessListener(aVoid -> {
                                        isBookmarked = false;
                                        updateBookmarkButton();
                                        Snackbar.make(findViewById(R.id.coordinator_layout), 
                                                "Removed from bookmarks", Snackbar.LENGTH_SHORT).show();
                                    });
                        }
                    });
        } else {
            userRepository.saveAnimeBookmark(animeData)
                    .addOnSuccessListener(documentReference -> {
                        isBookmarked = true;
                        updateBookmarkButton();
                        Snackbar.make(findViewById(R.id.coordinator_layout), 
                                "Added to bookmarks", Snackbar.LENGTH_SHORT).show();
                    });
        }
    }
    
    private void updateBookmarkButton() {
        if (isBookmarked) {
            bookmarkButton.setText(R.string.bookmark_removed);
            bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_bookmark_filled, 0, 0, 0);
        } else {
            bookmarkButton.setText(R.string.bookmark_added);
            bookmarkButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_bookmark_outline, 0, 0, 0);
        }
    }
    
    private void recommendSimilar() {
        Snackbar.make(recommendButton, "Recommendations coming soon", Snackbar.LENGTH_SHORT).show();
    }
    
    private void shareAnime(View view) {
        if (animeData == null) return;
        
        String shareText = getString(R.string.share_anime_text, animeData.getTitle()) + 
                "\nScore: " + animeData.getScore() + 
                "\nhttps://myanimelist.net/anime/" + animeId;
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
    
    private void savePreference(String status) {
        if (!userRepository.isUserLoggedIn()) {
            showError(getString(R.string.login_required));
            return;
        }
        
        userRepository.saveUserPreference(animeId, Bookmark.TYPE_ANIME, status)
                .addOnSuccessListener(documentReference -> {
                    // Preference saved successfully
                })
                .addOnFailureListener(e -> {
                    showError(getString(R.string.error_saving_preference));
                    // Reset chip state on failure
                    loadUserPreferences();
                });
    }
    
    private void removePreference(String status) {
        if (!userRepository.isUserLoggedIn()) {
            return;
        }
        
        userRepository.removeUserPreference(animeId, Bookmark.TYPE_ANIME)
                .addOnFailureListener(e -> {
                    showError(getString(R.string.error_saving_preference));
                    // Reset chip state on failure
                    loadUserPreferences();
                });
    }
    
    private void checkIfBookmarked() {
        if (!userRepository.isUserLoggedIn()) {
            return;
        }
        
        userRepository.isAnimeBookmarked(animeId)
                .addOnSuccessListener(isBookmarked -> {
                    this.isBookmarked = isBookmarked;
                    updateBookmarkButton();
                });
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 