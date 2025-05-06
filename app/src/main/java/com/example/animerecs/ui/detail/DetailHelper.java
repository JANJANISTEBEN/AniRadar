package com.example.animerecs.ui.detail;

import android.content.Context;
import android.content.Intent;

import com.example.animerecs.api.model.AnimeData;

public class DetailHelper {
    
    public static void openAnimeDetail(Context context, AnimeData anime) {
        Intent intent = new Intent(context, AnimeDetailActivity.class);
        intent.putExtra("anime_id", anime.getId());
        intent.putExtra("anime_title", anime.getTitle());
        intent.putExtra("anime_image_url", anime.getImages().getJpg().getLargeImageUrl());
        context.startActivity(intent);
    }
} 