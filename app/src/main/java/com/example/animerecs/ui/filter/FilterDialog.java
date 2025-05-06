package com.example.animerecs.ui.filter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.example.animerecs.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterDialog extends Dialog {

    // Content type constants based on Jikan API docs
    public static final String CONTENT_TYPE_ANIME = "ANIME";
    public static final String CONTENT_TYPE_MANGA = "MANGA";
    
    // Filter parameters
    private String contentType;
    private FilterListener listener;
    
    // UI Elements
    private Spinner typeSpinner;
    private Spinner statusSpinner;
    private Spinner orderBySpinner;
    private Spinner sortSpinner;
    private RangeSlider scoreRangeSlider;
    private ChipGroup genresChipGroup;
    private View seasonContainer;
    private Spinner seasonSpinner;
    private ImageButton cancelButton;
    private Button resetButton;
    private Button applyButton;
    
    // Filter data
    private Map<String, String> animeTypes = new HashMap<>();
    private Map<String, String> mangaTypes = new HashMap<>();
    private Map<String, String> animeStatuses = new HashMap<>();
    private Map<String, String> mangaStatuses = new HashMap<>();
    private Map<String, String> orderByOptions = new HashMap<>();
    private Map<String, String> sortOptions = new HashMap<>();
    private Map<String, String> seasons = new HashMap<>();
    private Map<Integer, String> genres = new HashMap<>();
    private List<Integer> selectedGenreIds = new ArrayList<>();

    public interface FilterListener {
        void onFilterApplied(FilterOptions options);
    }

    public FilterDialog(@NonNull Context context, String contentType, @Nullable FilterListener listener) {
        super(context);
        this.contentType = contentType;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter);
        
        // Initialize UI elements
        typeSpinner = findViewById(R.id.type_spinner);
        statusSpinner = findViewById(R.id.status_spinner);
        orderBySpinner = findViewById(R.id.order_by_spinner);
        sortSpinner = findViewById(R.id.sort_spinner);
        scoreRangeSlider = findViewById(R.id.score_range_slider);
        genresChipGroup = findViewById(R.id.genres_chip_group);
        seasonContainer = findViewById(R.id.season_container);
        seasonSpinner = findViewById(R.id.season_spinner);
        
        applyButton = findViewById(R.id.apply_button);
        resetButton = findViewById(R.id.reset_button);
        cancelButton = findViewById(R.id.cancel_button);
        
        // Set up data
        setupFilterData();
        
        // Configure the dialog based on content type
        if (CONTENT_TYPE_ANIME.equals(contentType)) {
            setupSpinner(typeSpinner, animeTypes);
            setupSpinner(statusSpinner, animeStatuses);
            setupSpinner(seasonSpinner, seasons);
            seasonContainer.setVisibility(View.VISIBLE);
        } else {
            setupSpinner(typeSpinner, mangaTypes);
            setupSpinner(statusSpinner, mangaStatuses);
            seasonContainer.setVisibility(View.GONE);
        }
        
        setupSpinner(orderBySpinner, orderByOptions);
        setupSpinner(sortSpinner, sortOptions);
        setupGenreChips();
        
        // Button actions
        applyButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterApplied(buildFilterOptions());
            }
            dismiss();
        });
        
        resetButton.setOnClickListener(v -> resetFilters());
        
        cancelButton.setOnClickListener(v -> dismiss());
    }
    
    private void setupFilterData() {
        // Anime types (from Jikan API)
        animeTypes.put("Any", "");
        animeTypes.put("TV", "tv");
        animeTypes.put("Movie", "movie");
        animeTypes.put("OVA", "ova");
        animeTypes.put("Special", "special");
        animeTypes.put("ONA", "ona");
        animeTypes.put("Music", "music");
        
        // Manga types
        mangaTypes.put("Any", "");
        mangaTypes.put("Manga", "manga");
        mangaTypes.put("Novel", "novel");
        mangaTypes.put("Light Novel", "lightnovel");
        mangaTypes.put("One-shot", "oneshot");
        mangaTypes.put("Doujin", "doujin");
        mangaTypes.put("Manhwa", "manhwa");
        mangaTypes.put("Manhua", "manhua");
        
        // Anime statuses
        animeStatuses.put("Any", "");
        animeStatuses.put("Airing", "airing");
        animeStatuses.put("Complete", "complete");
        animeStatuses.put("Upcoming", "upcoming");
        
        // Manga statuses
        mangaStatuses.put("Any", "");
        mangaStatuses.put("Publishing", "publishing");
        mangaStatuses.put("Complete", "complete");
        mangaStatuses.put("Hiatus", "hiatus");
        mangaStatuses.put("Discontinued", "discontinued");
        mangaStatuses.put("Upcoming", "upcoming");
        
        // Season options
        seasons.put("Any", "");
        seasons.put("Winter", "winter");
        seasons.put("Spring", "spring");
        seasons.put("Summer", "summer");
        seasons.put("Fall", "fall");
        
        // Order by options
        orderByOptions.put("Default", "");
        orderByOptions.put("Title", "title");
        orderByOptions.put("Start Date", "start_date");
        orderByOptions.put("End Date", "end_date");
        orderByOptions.put("Episodes/Chapters", CONTENT_TYPE_ANIME.equals(contentType) ? "episodes" : "chapters");
        orderByOptions.put("Score", "score");
        orderByOptions.put("Rank", "rank");
        orderByOptions.put("Popularity", "popularity");
        orderByOptions.put("Favorites", "favorites");
        
        // Sort options
        sortOptions.put("Descending", "desc");
        sortOptions.put("Ascending", "asc");
        
        // Common anime/manga genres (simplified list)
        genres.put(1, "Action");
        genres.put(2, "Adventure");
        genres.put(4, "Comedy");
        genres.put(7, "Mystery");
        genres.put(8, "Drama");
        genres.put(10, "Fantasy");
        genres.put(14, "Horror");
        genres.put(18, "Mecha");
        genres.put(22, "Romance");
        genres.put(24, "Sci-Fi");
        genres.put(27, "Shounen");
        genres.put(36, "Slice of Life");
        genres.put(37, "Supernatural");
        genres.put(41, "Thriller");
    }
    
    private void setupSpinner(Spinner spinner, Map<String, String> data) {
        String[] items = data.keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }
    
    private void setupGenreChips() {
        genresChipGroup.removeAllViews();
        
        for (Map.Entry<Integer, String> entry : genres.entrySet()) {
            Chip chip = (Chip) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_filter_chip, genresChipGroup, false);
            
            chip.setText(entry.getValue());
            chip.setTag(entry.getKey());
            chip.setCheckable(true);
            chip.setChecked(selectedGenreIds.contains(entry.getKey()));
            
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Integer genreId = (Integer) buttonView.getTag();
                if (isChecked) {
                    selectedGenreIds.add(genreId);
                } else {
                    selectedGenreIds.remove(genreId);
                }
            });
            
            genresChipGroup.addView(chip);
        }
    }
    
    private void resetFilters() {
        typeSpinner.setSelection(0);
        statusSpinner.setSelection(0);
        orderBySpinner.setSelection(0);
        sortSpinner.setSelection(0);
        scoreRangeSlider.setValues(0f, 10f);
        
        if (CONTENT_TYPE_ANIME.equals(contentType)) {
            seasonSpinner.setSelection(0);
        }
        
        selectedGenreIds.clear();
        
        for (int i = 0; i < genresChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) genresChipGroup.getChildAt(i);
            chip.setChecked(false);
        }
    }
    
    private FilterOptions buildFilterOptions() {
        FilterOptions options = new FilterOptions();
        
        Map<String, String> currentTypes = CONTENT_TYPE_ANIME.equals(contentType) ? animeTypes : mangaTypes;
        Map<String, String> currentStatuses = CONTENT_TYPE_ANIME.equals(contentType) ? animeStatuses : mangaStatuses;
        
        String typeKey = (String) typeSpinner.getSelectedItem();
        String statusKey = (String) statusSpinner.getSelectedItem();
        String orderByKey = (String) orderBySpinner.getSelectedItem();
        String sortKey = (String) sortSpinner.getSelectedItem();
        
        options.setType(currentTypes.get(typeKey));
        options.setStatus(currentStatuses.get(statusKey));
        options.setOrderBy(orderByOptions.get(orderByKey));
        options.setSort(sortOptions.get(sortKey));
        
        // Set season for anime only
        if (CONTENT_TYPE_ANIME.equals(contentType)) {
            String seasonKey = (String) seasonSpinner.getSelectedItem();
            options.setSeason(seasons.get(seasonKey));
        }
        
        List<Float> scoreValues = scoreRangeSlider.getValues();
        options.setMinScore(scoreValues.get(0));
        options.setMaxScore(scoreValues.get(1));
        
        options.setGenreIds(selectedGenreIds);
        
        return options;
    }
    
    public void setFilterOptions(FilterOptions options) {
        if (options == null) return;
        
        // Only set spinner values if they are initialized
        if (typeSpinner != null && options.getType() != null && !options.getType().isEmpty()) {
            setSpinnerFromValue(typeSpinner, CONTENT_TYPE_ANIME.equals(contentType) ? animeTypes : mangaTypes, options.getType());
        }
        
        if (statusSpinner != null && options.getStatus() != null && !options.getStatus().isEmpty()) {
            setSpinnerFromValue(statusSpinner, CONTENT_TYPE_ANIME.equals(contentType) ? animeStatuses : mangaStatuses, options.getStatus());
        }
        
        if (orderBySpinner != null && options.getOrderBy() != null && !options.getOrderBy().isEmpty()) {
            setSpinnerFromValue(orderBySpinner, orderByOptions, options.getOrderBy());
        }
        
        if (sortSpinner != null && options.getSort() != null && !options.getSort().isEmpty()) {
            setSpinnerFromValue(sortSpinner, sortOptions, options.getSort());
        }
        
        // Set season for anime
        if (CONTENT_TYPE_ANIME.equals(contentType) && seasonSpinner != null && 
            options.getSeason() != null && !options.getSeason().isEmpty()) {
            setSpinnerFromValue(seasonSpinner, seasons, options.getSeason());
        }
        
        // Set score range
        if (scoreRangeSlider != null && options.getMinScore() != null && options.getMaxScore() != null) {
            scoreRangeSlider.setValues(options.getMinScore(), options.getMaxScore());
        }
        
        // Set genres
        selectedGenreIds.clear();
        if (options.getGenreIds() != null) {
            selectedGenreIds.addAll(options.getGenreIds());
        }
        
        // Only refresh UI if the dialog is showing
        if (isShowing() && genresChipGroup != null) {
            setupGenreChips();
        }
    }
    
    private void setSpinnerFromValue(Spinner spinner, Map<String, String> dataMap, String value) {
        if (spinner == null || spinner.getAdapter() == null) return;
        
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            String key = (String) spinner.getAdapter().getItem(i);
            if (value.equals(dataMap.get(key))) {
                spinner.setSelection(i);
                break;
            }
        }
    }
} 