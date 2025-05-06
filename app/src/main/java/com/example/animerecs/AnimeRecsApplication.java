package com.example.animerecs;

import android.app.Application;

import com.example.animerecs.data.repository.ThemeManager;

public class AnimeRecsApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize ThemeManager
        ThemeManager.getInstance(this).initializeTheme();
    }
} 