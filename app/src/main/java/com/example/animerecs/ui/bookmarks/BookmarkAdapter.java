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
        void onBookmarkDelete(Bookmark bookmark);
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
        final Bookmark bookmark = bookmarks.get(position);
        
        holder.textTitle.setText(bookmark.getTitle());
        holder.textType.setText(bookmark.getType());
        
        // Load image with Glide
        Glide.with(context)
                .load(bookmark.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);
        
        // Handle click on the entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookmarkClick(bookmark);
            }
        });
        
        // Handle delete button click
        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookmarkDelete(bookmark);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return bookmarks.size();
    }
    
    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks != null ? bookmarks : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textTitle;
        TextView textType;
        ImageView buttonDelete;
        
        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_bookmark);
            textTitle = itemView.findViewById(R.id.text_title);
            textType = itemView.findViewById(R.id.text_type);
            buttonDelete = itemView.findViewById(R.id.btn_remove);
        }
    }
} 