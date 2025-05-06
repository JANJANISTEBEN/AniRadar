package com.example.animerecs.ui.detail;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.animerecs.R;
import com.example.animerecs.api.ApiClient;
import com.example.animerecs.api.model.AnimeData;
import com.example.animerecs.api.model.AnimeResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeDetailActivity extends AppCompatActivity {

    private ImageView animeImage;
    private TextView titleText;
    private TextView typeText;
    private TextView scoreText;
    private TextView statusText;
    private TextView episodesText;
    private TextView synopsisText;
    private TextView genresText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);
        
        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.anime_details);
        }
        
        // Initialize views
        animeImage = findViewById(R.id.anime_image);
        titleText = findViewById(R.id.anime_title);
        typeText = findViewById(R.id.anime_type);
        scoreText = findViewById(R.id.anime_score);
        statusText = findViewById(R.id.anime_status);
        episodesText = findViewById(R.id.anime_episodes);
        synopsisText = findViewById(R.id.anime_synopsis);
        genresText = findViewById(R.id.anime_genres);
        
        // Get anime ID from intent
        String animeId = getIntent().getStringExtra("anime_id");
        String animeTitle = getIntent().getStringExtra("anime_title");
        String imageUrl = getIntent().getStringExtra("anime_image_url");
        
        // Load basic data immediately
        titleText.setText(animeTitle);
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(animeImage);
        }
        
        // Load full anime details
        if (animeId != null) {
            loadAnimeDetails(animeId);
        }
    }
    
    private void loadAnimeDetails(String animeId) {
        ApiClient.getApi().getAnimeById(animeId).enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AnimeData anime = response.body().getData();
                    updateUI(anime);
                }
            }
            
            @Override
            public void onFailure(Call<AnimeResponse> call, Throwable t) {
                // Handle error
            }
        });
    }
    
    private void updateUI(AnimeData anime) {
        // Update UI with anime details
        titleText.setText(anime.getTitle());
        typeText.setText(anime.getType());
        scoreText.setText(String.valueOf(anime.getScore()));
        statusText.setText(anime.getStatus());
        episodesText.setText(String.valueOf(anime.getEpisodes()));
        synopsisText.setText(anime.getSynopsis());
        genresText.setText(anime.getGenresString());
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