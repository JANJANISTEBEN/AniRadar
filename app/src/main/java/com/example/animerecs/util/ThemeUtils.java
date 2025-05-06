package com.example.animerecs.util;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.example.animerecs.R;
import com.example.animerecs.data.repository.ThemeManager;

/**
 * Utility class for working with themes and colors
 */
public class ThemeUtils {

    /**
     * Get the current primary color based on the selected theme
     */
    @ColorInt
    public static int getPrimaryColor(Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            return ContextCompat.getColor(context, R.color.dark_primary);
        } else {
            return ContextCompat.getColor(context, R.color.light_primary);
        }
    }
    
    /**
     * Get the current primary dark color based on the selected theme
     */
    @ColorInt
    public static int getPrimaryDarkColor(Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            return ContextCompat.getColor(context, R.color.dark_primary_dark);
        } else {
            return ContextCompat.getColor(context, R.color.light_primary_dark);
        }
    }
    
    /**
     * Get the current background color based on the selected theme.
     */
    @ColorInt
    public static int getBackgroundColor(Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            return ContextCompat.getColor(context, R.color.dark_background);
        } else {
            return ContextCompat.getColor(context, R.color.light_background);
        }
    }
    
    /**
     * Get the appropriate text color for primary text based on the current theme
     */
    @ColorInt
    public static int getPrimaryTextColor(Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            return ContextCompat.getColor(context, R.color.dark_text_primary);
        } else {
            return ContextCompat.getColor(context, R.color.light_text_primary);
        }
    }
    
    /**
     * Get the appropriate text color for secondary text based on the current theme
     */
    @ColorInt
    public static int getSecondaryTextColor(Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            return ContextCompat.getColor(context, R.color.dark_text_secondary);
        } else {
            return ContextCompat.getColor(context, R.color.light_text_secondary);
        }
    }
    
    /**
     * Get card background color based on the selected theme.
     */
    @ColorInt
    public static int getCardBackgroundColor(Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            return ContextCompat.getColor(context, R.color.dark_card_background);
        } else {
            return ContextCompat.getColor(context, R.color.light_card_background);
        }
    }
    
    /**
     * Get the accent color appropriate for the current theme
     */
    @ColorInt
    public static int getAccentColor(Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            return ContextCompat.getColor(context, R.color.dark_accent);
        } else {
            return ContextCompat.getColor(context, R.color.light_accent);
        }
    }
    
    /**
     * Check if the current theme is dark theme
     */
    public static boolean isDarkTheme(Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        return themeManager.getCurrentTheme() == ThemeManager.THEME_DARK;
    }
} 