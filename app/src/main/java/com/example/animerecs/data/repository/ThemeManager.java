package com.example.animerecs.data.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Manages theme settings for the app
 */
public class ThemeManager {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    
    // Theme constants
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    
    private static ThemeManager instance;
    private final SharedPreferences preferences;
    private final Context context;
    private Activity currentActivity; // Track current activity for recreation
    private boolean isChangingTheme = false; // Flag to prevent multiple recreations
    
    private ThemeManager(Context context) {
        this.context = context.getApplicationContext();
        preferences = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        
        // If context is an activity, update current activity reference
        if (context instanceof Activity) {
            instance.currentActivity = (Activity) context;
        }
        
        return instance;
    }
    
    /**
     * Set dark mode on or off
     * @param isDarkMode true for dark mode, false for light mode
     */
    public void setDarkMode(boolean isDarkMode) {
        // Prevent multiple changes at once
        if (isChangingTheme) {
            return;
        }
        
        // Only make changes if setting actually changed
        if (isDarkMode != isDarkModeEnabled()) {
            isChangingTheme = true;
            
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_DARK_MODE, isDarkMode);
            editor.apply();
            
            applyTheme();
            
            // Always use full restart for dark mode changes to ensure consistent application
            restartApp();
        }
    }
    
    /**
     * Check if dark mode is enabled
     * @return true if dark mode is enabled, false otherwise
     */
    public boolean isDarkModeEnabled() {
        return preferences.getBoolean(KEY_DARK_MODE, false);
    }
    
    /**
     * Get the current theme (light or dark)
     * @return The current theme constant (THEME_LIGHT or THEME_DARK)
     */
    public int getCurrentTheme() {
        return isDarkModeEnabled() ? THEME_DARK : THEME_LIGHT;
    }
    
    /**
     * Set theme
     * @param theme The theme to use: THEME_LIGHT or THEME_DARK
     */
    public void setTheme(int theme) {
        // Prevent multiple changes at once
        if (isChangingTheme) {
            return;
        }
        
        boolean shouldBeDarkMode = theme == THEME_DARK;
        
        // Only make changes if setting actually changed
        if (shouldBeDarkMode != isDarkModeEnabled()) {
            isChangingTheme = true;
            
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_DARK_MODE, shouldBeDarkMode);
            editor.apply();
            
            // Apply theme and restart for all changes
            applyTheme();
            restartApp();
        }
    }
    
    /**
     * Apply the current theme setting immediately
     */
    public void applyTheme() {
        // Apply night mode immediately
        boolean isDarkMode = isDarkModeEnabled();
        int nightMode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
    
    /**
     * Restart the app completely to ensure themes are properly applied
     * This is more reliable than activity recreation for complex theme changes
     */
    private void restartApp() {
        if (currentActivity != null && !currentActivity.isFinishing()) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (currentActivity != null) {
                    // Get the intent that started the app
                    Intent intent = currentActivity.getPackageManager()
                            .getLaunchIntentForPackage(currentActivity.getPackageName());
                    
                    if (intent != null) {
                        // Add flags to clear the task and start fresh
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        
                        // Start the app again
                        currentActivity.startActivity(intent);
                        currentActivity.finish();
                        
                        // Optional: add a transition animation to make it smoother
                        currentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
                // Reset flag after restart
                isChangingTheme = false;
            }, 100);
        } else {
            // Reset flag if no activity to restart
            isChangingTheme = false;
        }
    }
    
    /**
     * Initialize theme settings based on saved preferences.
     * Call this in MainActivity's onCreate before setContentView.
     */
    public void initializeTheme() {
        // Reset changing flag on initialization
        isChangingTheme = false;
        
        applyTheme();
    }
} 