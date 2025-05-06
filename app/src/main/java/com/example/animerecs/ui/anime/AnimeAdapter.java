package com.example.animerecs.ui.anime;

import android.content.Context;
import android.util.Log;
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
import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.data.repository.BookmarkRepository;

import java.util.ArrayList;
import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {

    private final Context context;
    private List<AnimeData> animeList;
    private final OnAnimeClickListener listener;
    private BookmarkRepository bookmarkRepository;

    public interface OnAnimeClickListener {
        void onAnimeClick(AnimeData anime);
        void onBookmarkClick(AnimeData anime, boolean isCurrentlyBookmarked);
    }

    public AnimeAdapter(Context context, List<AnimeData> animeList, OnAnimeClickListener listener) {
        this.context = context;
        this.animeList = animeList != null ? new ArrayList<>(animeList) : new ArrayList<>();
        this.listener = listener;
        this.bookmarkRepository = new BookmarkRepository();
        Log.d("AnimeAdapter", "New adapter created with " + this.animeList.size() + " items");
        if (!this.animeList.isEmpty()) {
            Log.d("AnimeAdapter", "First item in constructor: " + this.animeList.get(0).getTitle());
        }
    }

    public void updateData(List<AnimeData> newList) {
        Log.d("AnimeAdapter", "updateData called with " + (newList != null ? newList.size() : 0) + " items");
        if (newList != null) {
            this.animeList = new ArrayList<>(newList);
            if (!newList.isEmpty()) {
                Log.d("AnimeAdapter", "First item title: " + newList.get(0).getTitle());
            }
            notifyDataSetChanged();
            Log.d("AnimeAdapter", "notifyDataSetChanged called from updateData");
        }
    }
    
    public void setAnimeList(List<AnimeData> animeList) {
        Log.d("AnimeAdapter", "setAnimeList called with " + (animeList != null ? animeList.size() : 0) + " items");
        if (animeList != null) {
            // Clear and replace the entire list to ensure clean state
            this.animeList.clear();
            this.animeList.addAll(animeList);
            if (!animeList.isEmpty()) {
                Log.d("AnimeAdapter", "First item title: " + animeList.get(0).getTitle());
            }
            notifyDataSetChanged();
            Log.d("AnimeAdapter", "notifyDataSetChanged called from setAnimeList");
        }
    }
    
    /**
     * Completely replaces the adapter data with a fresh list
     * This helps with ensuring the UI gets fully refreshed
     */
    public void forceRefresh(List<AnimeData> animeList) {
        Log.d("AnimeAdapter", "forceRefresh called with " + (animeList != null ? animeList.size() : 0) + " items");
        if (animeList != null) {
            // Create a completely new list
            this.animeList = new ArrayList<>();
            
            // Add each item individually to ensure complete refresh
            if (!animeList.isEmpty()) {
                for (AnimeData item : animeList) {
                    this.animeList.add(item);
                }
                Log.d("AnimeAdapter", "First item in forceRefresh: " + animeList.get(0).getTitle());
                Log.d("AnimeAdapter", "First item score: " + animeList.get(0).getScore());
                Log.d("AnimeAdapter", "First item type: " + animeList.get(0).getType());
            }
            
            // Force complete refresh
            notifyDataSetChanged();
            Log.d("AnimeAdapter", "forceRefresh complete with notifyDataSetChanged");
        }
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_anime, parent, false);
        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        AnimeData anime = animeList.get(position);
        
        android.util.Log.d("AnimeAdapter", "Binding item at position " + position + ": " + anime.getTitle());
        
        // Set title and synopsis
        holder.title.setText(anime.getTitle());
        holder.synopsis.setText(anime.getSynopsis());
        
        // Set type and episodes
        holder.type.setText(anime.getType());
        
        // Handle episodes - some anime might not have episode count
        int episodes = anime.getEpisodes();
        holder.episodes.setText(episodes > 0 ? episodes + " episodes" : "Unknown episodes");
        
        // Set score
        holder.score.setText(String.valueOf(anime.getScore()));
        
        // Set genres
        holder.genres.setText(anime.getGenresString());
        
        // Log the image URL to debug
        String imageUrl = anime.getImageUrl();
        android.util.Log.d("AnimeAdapter", "Image URL for " + anime.getTitle() + ": " + imageUrl);
        
        // Load image with Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_anime)
                .error(R.drawable.ic_anime)
                .into(holder.image);
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnimeClick(anime);
            }
        });
        
        // Set up bookmark button
        if (holder.btnBookmark != null) {
            // Check if this anime is bookmarked
            bookmarkRepository.checkIfBookmarked(anime.getId(), "anime", isBookmarked -> {
                // Update bookmark icon based on bookmark status
                holder.btnBookmark.setImageResource(
                        isBookmarked ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);
                
                // Set click listener for bookmark button
                holder.btnBookmark.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onBookmarkClick(anime, isBookmarked);
                        
                        // Toggle the icon (optimistic UI update)
                        holder.btnBookmark.setImageResource(
                                !isBookmarked ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = animeList != null ? animeList.size() : 0;
        Log.d("AnimeAdapter", "getItemCount returning " + count);
        return count;
    }

    static class AnimeViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, synopsis, type, episodes, score, genres;
        ImageView btnBookmark;

        AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.anime_image);
            title = itemView.findViewById(R.id.anime_title);
            synopsis = itemView.findViewById(R.id.anime_synopsis);
            type = itemView.findViewById(R.id.anime_type);
            episodes = itemView.findViewById(R.id.anime_episodes);
            score = itemView.findViewById(R.id.anime_score);
            genres = itemView.findViewById(R.id.anime_genres);
            btnBookmark = itemView.findViewById(R.id.btn_bookmark);
        }
    }
} 