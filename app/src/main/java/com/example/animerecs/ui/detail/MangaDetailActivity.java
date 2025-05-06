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
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.api.model.MangaResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MangaDetailActivity extends AppCompatActivity {

    private ImageView mangaImage;
    private TextView titleText;
    private TextView typeText;
    private TextView scoreText;
    private TextView statusText;
    private TextView chaptersText;
    private TextView volumesText;
    private TextView synopsisText;
    private TextView genresText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_detail);
        
        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.manga_details);
        }
        
        // Initialize views
        mangaImage = findViewById(R.id.manga_image);
        titleText = findViewById(R.id.manga_title);
        typeText = findViewById(R.id.manga_type);
        scoreText = findViewById(R.id.manga_score);
        statusText = findViewById(R.id.manga_status);
        chaptersText = findViewById(R.id.manga_chapters);
        volumesText = findViewById(R.id.manga_volumes);
        synopsisText = findViewById(R.id.manga_synopsis);
        genresText = findViewById(R.id.manga_genres);
        
        // Get manga ID from intent
        String mangaId = getIntent().getStringExtra("manga_id");
        String mangaTitle = getIntent().getStringExtra("manga_title");
        String imageUrl = getIntent().getStringExtra("manga_image_url");
        
        // Load basic data immediately
        titleText.setText(mangaTitle);
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .into(mangaImage);
        }
        
        // Load full manga details
        if (mangaId != null) {
            loadMangaDetails(mangaId);
        }
    }
    
    private void loadMangaDetails(String mangaId) {
        ApiClient.getApi().getMangaById(mangaId).enqueue(new Callback<MangaResponse>() {
            @Override
            public void onResponse(Call<MangaResponse> call, Response<MangaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MangaData manga = response.body().getData();
                    updateUI(manga);
                }
            }
            
            @Override
            public void onFailure(Call<MangaResponse> call, Throwable t) {
                // Handle error
            }
        });
    }
    
    private void updateUI(MangaData manga) {
        // Update UI with manga details
        titleText.setText(manga.getTitle());
        typeText.setText(manga.getType());
        scoreText.setText(String.valueOf(manga.getScore()));
        statusText.setText(manga.getStatus());
        chaptersText.setText(String.valueOf(manga.getChapters()));
        volumesText.setText(String.valueOf(manga.getVolumes()));
        synopsisText.setText(manga.getSynopsis());
        genresText.setText(manga.getGenresString());
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