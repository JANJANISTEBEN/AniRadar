package com.example.animerecs.ui.bookmarks;

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
import com.example.animerecs.data.model.Bookmark;

import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {
    
    private Context context;
    private List<Bookmark> bookmarks;
    private OnBookmarkClickListener listener;
    
    public interface OnBookmarkClickListener {
        void onBookmarkClick(Bookmark bookmark);
        void onBookmarkRemove(Bookmark bookmark);
    }
    
    public BookmarkAdapter(Context context, List<Bookmark> bookmarks, OnBookmarkClickListener listener) {
        this.context = context;
        this.bookmarks = bookmarks != null ? bookmarks : new ArrayList<>();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);
        
        holder.textTitle.setText(bookmark.getTitle());
        holder.textType.setText(bookmark.getType());
        holder.textScore.setText(String.valueOf(bookmark.getScore()));
        
        Glide.with(context)
                .load(bookmark.getImageUrl())
                .placeholder(bookmark.getType().equals("anime") ? 
                        R.drawable.placeholder_anime : R.drawable.placeholder_manga)
                .error(bookmark.getType().equals("anime") ? 
                        R.drawable.placeholder_anime : R.drawable.placeholder_manga)
                .into(holder.imageBookmark);
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookmarkClick(bookmark);
            }
        });
        
        // Set remove button click listener
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookmarkRemove(bookmark);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return bookmarks.size();
    }
    
    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
        notifyDataSetChanged();
    }
    
    public static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBookmark;
        TextView textTitle;
        TextView textType;
        TextView textScore;
        ImageView btnRemove;
        
        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBookmark = itemView.findViewById(R.id.image_bookmark);
            textTitle = itemView.findViewById(R.id.text_title);
            textType = itemView.findViewById(R.id.text_type);
            textScore = itemView.findViewById(R.id.text_score);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
} 