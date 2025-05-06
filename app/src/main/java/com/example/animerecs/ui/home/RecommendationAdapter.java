package com.example.animerecs.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.animerecs.R;
import com.example.animerecs.api.model.AnimeData;
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.data.repository.BookmarkRepository;

import java.util.ArrayList;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    private static final int TYPE_ANIME = 0;
    private static final int TYPE_MANGA = 1;

    private Context context;
    private List<?> items; // Can hold either AnimeData or MangaData
    private int itemType; // 0 for anime, 1 for manga
    private OnRecommendationClickListener listener;
    private BookmarkRepository bookmarkRepository;

    public interface OnRecommendationClickListener {
        void onItemClick(Object item);
        void onBookmarkClick(Object item, boolean isCurrentlyBookmarked);
    }

    public RecommendationAdapter(Context context, List<?> items, int itemType, OnRecommendationClickListener listener) {
        this.context = context;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.itemType = itemType;
        this.listener = listener;
        this.bookmarkRepository = new BookmarkRepository();
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        Object item = items.get(position);
        
        String title = "";
        String imageUrl = "";
        float score = 0f;
        String type = "";
        int id = 0;
        
        if (itemType == TYPE_ANIME && item instanceof AnimeData) {
            AnimeData anime = (AnimeData) item;
            title = anime.getTitle();
            imageUrl = anime.getImageUrl();
            score = (float) anime.getScore();
            type = anime.getType();
            id = anime.getId();
        } else if (itemType == TYPE_MANGA && item instanceof MangaData) {
            MangaData manga = (MangaData) item;
            title = manga.getTitle();
            imageUrl = manga.getImageUrl();
            score = (float) manga.getScore();
            type = manga.getType();
            id = manga.getId();
        }
        
        holder.textTitle.setText(title);
        holder.textType.setText(type);
        holder.textScore.setText(String.valueOf(score));
        
        Glide.with(context)
                .load(imageUrl)
                .placeholder(itemType == TYPE_ANIME ? R.drawable.placeholder_anime : R.drawable.placeholder_manga)
                .error(itemType == TYPE_ANIME ? R.drawable.placeholder_anime : R.drawable.placeholder_manga)
                .into(holder.imageRecommendation);
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
        
        // Check if bookmarked
        final String itemId = String.valueOf(id);
        final String itemType = this.itemType == TYPE_ANIME ? "anime" : "manga";
        bookmarkRepository.checkIfBookmarked(itemId, itemType, isBookmarked -> {
            // Update bookmark icon
            holder.btnBookmark.setImageResource(
                    isBookmarked ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);
            
            // Set click listener for bookmark button
            holder.btnBookmark.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookmarkClick(item, isBookmarked);
                    
                    // Toggle the icon (optimistic UI update)
                    holder.btnBookmark.setImageResource(
                            !isBookmarked ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<?> newItems) {
        if (newItems != null) {
            this.items = new ArrayList<>(newItems);
            notifyDataSetChanged();
        }
    }

    static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        ImageView imageRecommendation;
        ImageView btnBookmark;
        TextView textTitle;
        TextView textType;
        TextView textScore;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageRecommendation = itemView.findViewById(R.id.image_recommendation);
            btnBookmark = itemView.findViewById(R.id.btn_bookmark);
            textTitle = itemView.findViewById(R.id.text_title);
            textType = itemView.findViewById(R.id.text_type);
            textScore = itemView.findViewById(R.id.text_score);
        }
    }
} 