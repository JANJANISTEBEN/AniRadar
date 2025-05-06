package com.example.animerecs.ui.manga;

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
import com.example.animerecs.api.model.MangaData;
import com.example.animerecs.data.repository.BookmarkRepository;
import com.example.animerecs.data.model.Bookmark;

import java.util.List;

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.MangaViewHolder> {

    private final Context context;
    private List<MangaData> mangaList;
    private final OnMangaClickListener listener;
    private final BookmarkRepository bookmarkRepository;

    public interface OnMangaClickListener {
        void onMangaClick(MangaData manga);
        void onBookmarkClick(MangaData manga, boolean isCurrentlyBookmarked);
    }

    public MangaAdapter(Context context, List<MangaData> mangaList, OnMangaClickListener listener) {
        this.context = context;
        this.mangaList = mangaList;
        this.listener = listener;
        this.bookmarkRepository = new BookmarkRepository();
    }

    public void updateData(List<MangaData> newList) {
        this.mangaList = newList;
        notifyDataSetChanged();
    }

    public void setMangaList(List<MangaData> newList) {
        this.mangaList = newList;
        notifyDataSetChanged();
    }
    
    public void forceRefresh(List<MangaData> newList) {
        this.mangaList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manga, parent, false);
        return new MangaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaViewHolder holder, int position) {
        MangaData manga = mangaList.get(position);
        
        // Set title and synopsis
        holder.title.setText(manga.getTitle());
        holder.synopsis.setText(manga.getSynopsis());
        
        // Set type and volumes
        holder.type.setText(manga.getType());
        holder.volumes.setText(manga.getVolumes() > 0 ? manga.getVolumes() + " volumes" : "Ongoing");
        
        // Set score
        holder.score.setText(String.valueOf(manga.getScore()));
        
        // Set genres
        holder.genres.setText(manga.getGenresString());
        
        // Load image with Glide
        Glide.with(context)
                .load(manga.getImageUrl())
                .placeholder(R.drawable.ic_manga)
                .into(holder.image);
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMangaClick(manga);
            }
        });
        
        // Check if manga is bookmarked
        bookmarkRepository.checkIfBookmarked(manga.getMalId(), Bookmark.TYPE_MANGA, isBookmarked -> {
            // Update bookmark icon based on status
            holder.bookmarkIcon.setImageResource(isBookmarked ? 
                    R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border);
            
            // Set bookmark click listener
            holder.bookmarkIcon.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookmarkClick(manga, isBookmarked);
                    // Toggle bookmark icon immediately for better UX
                    holder.bookmarkIcon.setImageResource(isBookmarked ? 
                            R.drawable.ic_bookmark_border : R.drawable.ic_bookmark_filled);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return mangaList != null ? mangaList.size() : 0;
    }

    static class MangaViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView bookmarkIcon;
        TextView title, synopsis, type, volumes, score, genres;

        MangaViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.manga_image);
            title = itemView.findViewById(R.id.manga_title);
            synopsis = itemView.findViewById(R.id.manga_synopsis);
            type = itemView.findViewById(R.id.manga_type);
            volumes = itemView.findViewById(R.id.manga_volumes);
            score = itemView.findViewById(R.id.manga_score);
            genres = itemView.findViewById(R.id.manga_genres);
            bookmarkIcon = itemView.findViewById(R.id   .btn_bookmark);
        }
    }
} 