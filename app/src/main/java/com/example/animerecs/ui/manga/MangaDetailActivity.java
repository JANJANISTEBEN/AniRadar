package com.example.animerecs.ui.manga;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.api.model.MangaResponse;
import com.example.animerecs.data.repository.ThemeManager;
import com.example.animerecs.model.Bookmark;
import com.example.animerecs.model.UserPreference;
import com.example.animerecs.repository.UserRepository;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaDetailActivity extends AppCompatActivity {
    
    private static final String EXTRA_MANGA_ID = "manga_id";
    
    private ImageView coverImageView;
    private TextView titleTextView;
    private TextView scoreTextView;
    private TextView typeTextView;
    private TextView volumesTextView;
    private TextView chaptersTextView;
    private TextView publishedTextView;
    private TextView genresTextView;
    private TextView synopsisTextView;
    private Button bookmarkButton;
    private Button recommendButton;
    private ChipGroup preferencesChipGroup;
    private Chip likeChip;
    private Chip dislikeChip;
    private Chip readChip;
    private Chip readLaterChip;
    private FloatingActionButton shareFab;
    
    private UserRepository userRepository;
    private String mangaId;
    private MangaData mangaData;
    private boolean isBookmarked = false;
    
    public static Intent newIntent(Context context, String mangaId) {
        Intent intent = new Intent(context, MangaDetailActivity.class);
        intent.putExtra(EXTRA_MANGA_ID, mangaId);
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize theme before setting the content view
        ThemeManager.getInstance(this).initializeTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_detail);
        
        // Apply theme colors to all views
        com.example.animerecs.util.ColorUtils.applyThemeColors(findViewById(android.R.id.content), this);
        
        userRepository = UserRepository.getInstance();
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Find views
        coverImageView = findViewById(R.id.manga_cover);
        titleTextView = findViewById(R.id.manga_title);
        scoreTextView = findViewById(R.id.manga_score);
        typeTextView = findViewById(R.id.manga_type);
        volumesTextView = findViewById(R.id.manga_volumes);
        chaptersTextView = findViewById(R.id.manga_chapters);
        publishedTextView = findViewById(R.id.manga_published);
        genresTextView = findViewById(R.id.manga_genres);
        synopsisTextView = findViewById(R.id.manga_synopsis);
        bookmarkButton = findViewById(R.id.btn_bookmark);
        recommendButton = findViewById(R.id.btn_recommend_similar);
        
        preferencesChipGroup = findViewById(R.id.manga_preferences);
        
        likeChip = findViewById(R.id.chip_like);
        dislikeChip = findViewById(R.id.chip_dislike);
        readChip = findViewById(R.id.chip_read);
        readLaterChip = findViewById(R.id.chip_read_later);
        
        shareFab = findViewById(R.id.fab_share);
        
        // Get manga ID from intent
        if (getIntent().hasExtra(EXTRA_MANGA_ID)) {
            mangaId = getIntent().getStringExtra(EXTRA_MANGA_ID);
            if (mangaId != null) {
                loadMangaDetails();
                checkIfBookmarked();
                loadUserPreferences();
            } else {
                showError("Manga ID not found");
                finish();
            }
        } else {
            showError("Manga ID not found");
            finish();
        }
        
        // Set up click listeners
        bookmarkButton.setOnClickListener(v -> toggleBookmark());
        recommendButton.setOnClickListener(v -> recommendSimilar());
        shareFab.setOnClickListener(v -> shareManga());
        
        setupChipListeners();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Re-apply theme colors when activity resumes
        com.example.animerecs.util.ColorUtils.applyThemeColors(findViewById(android.R.id.content), this);
    }
    
    private void setupChipListeners() {
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
        
        readChip.setOnClickListener(v -> {
            if (readChip.isChecked()) {
                savePreference(UserPreference.STATUS_READ);
                readLaterChip.setChecked(false);
            } else {
                removePreference(UserPreference.STATUS_READ);
            }
        });
        
        readLaterChip.setOnClickListener(v -> {
            if (readLaterChip.isChecked()) {
                savePreference(UserPreference.STATUS_READ_LATER);
                readChip.setChecked(false);
            } else {
                removePreference(UserPreference.STATUS_READ_LATER);
            }
        });
    }
    
    private void loadMangaDetails() {
        ApiClient.getApi().getMangaById(mangaId).enqueue(new Callback<MangaResponse>() {
            @Override
            public void onResponse(@NonNull Call<MangaResponse> call, @NonNull Response<MangaResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    mangaData = response.body().getData();
                    displayMangaDetails(mangaData);
                } else {
                    showError("Failed to load manga details");
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<MangaResponse> call, @NonNull Throwable t) {
                showError("Network error: " + t.getMessage());
            }
        });
    }
    
    private void displayMangaDetails(MangaData manga) {
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(manga.getTitle());
        
        titleTextView.setText(manga.getTitle());
        
        // Set score
        scoreTextView.setText(String.valueOf(manga.getScore()));
        
        // Set type
        typeTextView.setText(manga.getType());
        
        // Set volumes and chapters
        volumesTextView.setText(manga.getVolumes() + " volumes");
        chaptersTextView.setText(manga.getChapters() + " chapters");
        
        // Set published date
        publishedTextView.setText("Published: " + manga.getPublishedString());
        
        // Set genres
        genresTextView.setText(manga.getGenresString());
        
        // Set synopsis
        synopsisTextView.setText(manga.getSynopsis());
        
        // Load image with Glide
        Glide.with(this)
                .load(manga.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(coverImageView);
    }
    
    private void checkIfBookmarked() {
        if (!userRepository.isUserLoggedIn()) {
            return;
        }
        
        userRepository.isMangaBookmarked(mangaId)
                .addOnSuccessListener(isBookmarked -> {
                    this.isBookmarked = isBookmarked;
                    updateBookmarkButton();
                });
    }
    
    private void loadUserPreferences() {
        if (!userRepository.isUserLoggedIn()) {
            preferencesChipGroup.setVisibility(View.GONE);
            return;
        }
        
        preferencesChipGroup.setVisibility(View.VISIBLE);
        
        userRepository.getUserItemPreference(mangaId, Bookmark.TYPE_MANGA)
                .addOnSuccessListener(preference -> {
                    if (preference != null) {
                        likeChip.setChecked(preference.isLiked());
                        dislikeChip.setChecked(preference.isDisliked());
                        readChip.setChecked(preference.isRead());
                        readLaterChip.setChecked(preference.isReadLater());
                    } else {
                        likeChip.setChecked(false);
                        dislikeChip.setChecked(false);
                        readChip.setChecked(false);
                        readLaterChip.setChecked(false);
                    }
                });
    }
    
    private void savePreference(String status) {
        if (!userRepository.isUserLoggedIn()) {
            showError(getString(R.string.login_required));
            return;
        }
        
        userRepository.saveUserPreference(mangaId, Bookmark.TYPE_MANGA, status)
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
        
        userRepository.removeUserPreference(mangaId, Bookmark.TYPE_MANGA)
                .addOnFailureListener(e -> {
                    showError(getString(R.string.error_saving_preference));
                    // Reset chip state on failure
                    loadUserPreferences();
                });
    }
    
    private void toggleBookmark() {
        if (mangaData == null) return;
        
        if (isBookmarked) {
            // Get the bookmark document and delete it
            userRepository.getBookmarkByItemId(mangaId, Bookmark.TYPE_MANGA)
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
            // Add bookmark
            userRepository.saveMangaBookmark(mangaData)
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
        // TODO: Implement recommendation feature
        Snackbar.make(recommendButton, "Recommendations coming soon", Snackbar.LENGTH_SHORT).show();
    }
    
    private void shareManga() {
        if (mangaData == null) return;
        
        String shareText = getString(R.string.share_manga_text, mangaData.getTitle()) + 
                "\nScore: " + mangaData.getScore() + 
                "\nhttps://myanimelist.net/manga/" + mangaId;
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 