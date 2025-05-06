package com.example.animerecs.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.animerecs.R;
import com.example.animerecs.data.repository.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Utility class for applying colors to views based on the current theme
 */
public class ColorUtils {

    /**
     * Apply proper text colors to all TextViews in a view hierarchy
     * 
     * @param rootView The root view to traverse
     * @param context The context to use for getting colors
     */
    public static void applyTextColors(View rootView, Context context) {
        if (rootView instanceof ViewGroup) {
            applyTextColorsRecursively((ViewGroup) rootView, context);
        } else if (rootView instanceof TextView) {
            applyTextColorToView((TextView) rootView, context);
        }
    }
    
    /**
     * Apply background colors to the main container views in a view hierarchy
     * 
     * @param rootView The root view to traverse
     * @param context The context to use for getting colors
     */
    public static void applyBackgroundColors(View rootView, Context context) {
        // Apply background color to root view first
        int backgroundColor = ThemeUtils.getBackgroundColor(context);
        
        if (!(rootView instanceof CardView)) {
            rootView.setBackgroundColor(backgroundColor);
        }
        
        // Then recursively apply to children
        if (rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            
            // Check if this is a bottom navigation view - apply special styling
            if (viewGroup instanceof BottomNavigationView) {
                styleBottomNavigationView((BottomNavigationView) viewGroup, context);
            }
            
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                
                // Only apply to layout containers, not to CardViews
                if (child instanceof ViewGroup && 
                    !(child instanceof CardView) && 
                    !(child instanceof FragmentContainerView)) {
                    
                    // Apply background color to main containers
                    if (isMainContainer(child)) {
                        child.setBackgroundColor(backgroundColor);
                    }
                    
                    // Continue recursion
                    applyBackgroundColors(child, context);
                }
            }
        }
    }
    
    /**
     * Apply specific styling to bottom navigation view based on theme
     */
    private static void styleBottomNavigationView(BottomNavigationView navigationView, Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        // Set the background color based on theme
        int backgroundColor;
        if (currentTheme == ThemeManager.THEME_DARK) {
            backgroundColor = ContextCompat.getColor(context, R.color.dark_primary_dark);
        } else {
            backgroundColor = ContextCompat.getColor(context, R.color.light_primary_dark);
        }
        
        navigationView.setBackgroundColor(backgroundColor);
        
        // Create color state list for item colors - different for dark vs light themes
        int textColor, textColorInactive;
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            // Use white colors for dark theme
            textColor = ContextCompat.getColor(context, R.color.white);
            textColorInactive = ContextCompat.getColor(context, R.color.white_70);
            
            // Add white outline for dark theme
            navigationView.setElevation(8f);
            navigationView.setOutlineAmbientShadowColor(ContextCompat.getColor(context, R.color.white_70));
            navigationView.setOutlineSpotShadowColor(ContextCompat.getColor(context, R.color.white_70));
            
        } else {
            // Use white colors for light themes to contrast with darker nav bar
            textColor = ContextCompat.getColor(context, R.color.white);
            textColorInactive = ContextCompat.getColor(context, R.color.white_70);
            
            // Add black outline for light themes
            navigationView.setElevation(8f);
            navigationView.setOutlineAmbientShadowColor(ContextCompat.getColor(context, R.color.black));
            navigationView.setOutlineSpotShadowColor(ContextCompat.getColor(context, R.color.black));
        }
        
        ColorStateList colorStateList = new ColorStateList(
            new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] { -android.R.attr.state_checked }
            },
            new int[] {
                textColor,           // checked
                textColorInactive    // unchecked
            }
        );
        
        // Apply colors to bottom navigation
        navigationView.setItemTextColor(colorStateList);
        navigationView.setItemIconTintList(colorStateList);
    }
    
    /**
     * Check if a view is a main container that should have the theme background color
     */
    private static boolean isMainContainer(View view) {
        return view instanceof ConstraintLayout || 
               (view.getClass().getSimpleName().contains("Layout") && 
                !(view instanceof CardView));
    }
    
    /**
     * Apply card background colors to all CardViews in a view hierarchy
     * 
     * @param rootView The root view to traverse
     * @param context The context to use for getting colors
     */
    public static void applyCardColors(View rootView, Context context) {
        if (rootView instanceof ViewGroup) {
            applyCardColorsRecursively((ViewGroup) rootView, context);
        } else if (rootView instanceof CardView) {
            applyCardColorToView((CardView) rootView, context);
        }
    }
    
    /**
     * Apply both text and background colors to a view hierarchy
     * 
     * @param rootView The root view to traverse
     * @param context The context to use for getting colors
     */
    public static void applyThemeColors(View rootView, Context context) {
        // First apply background colors to containers
        applyBackgroundColors(rootView, context);
        
        // Then apply text colors as they appear on those backgrounds
        applyTextColors(rootView, context);
        
        // Apply card colors for all themes consistently
        applyCardColors(rootView, context);
        
        // Apply colors to any SearchViews
        applySearchViewColors(rootView, context);
        
        // Apply colors to navigation arrows and other icons
        applyIconColors(rootView, context);
    }
    
    /**
     * Apply text colors recursively to all TextViews in a ViewGroup
     */
    private static void applyTextColorsRecursively(ViewGroup viewGroup, Context context) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            
            if (child instanceof TextView) {
                applyTextColorToView((TextView) child, context);
            }
            
            if (child instanceof ViewGroup) {
                applyTextColorsRecursively((ViewGroup) child, context);
            }
        }
    }
    
    /**
     * Apply card colors recursively to all CardViews in a ViewGroup
     */
    private static void applyCardColorsRecursively(ViewGroup viewGroup, Context context) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            
            if (child instanceof CardView) {
                applyCardColorToView((CardView) child, context);
            }
            
            if (child instanceof ViewGroup) {
                applyCardColorsRecursively((ViewGroup) child, context);
            }
        }
    }
    
    /**
     * Apply appropriate text color to a TextView based on the current theme
     */
    private static void applyTextColorToView(TextView textView, Context context) {
        // Skip certain TextViews that should maintain their own colors
        if (isSpecialTextView(textView)) {
            return;
        }
        
        // Get the parent view to check if this TextView is inside a CardView
        boolean isInsideCardView = false;
        View parent = (View) textView.getParent();
        while (parent != null) {
            if (parent instanceof CardView) {
                isInsideCardView = true;
                break;
            }
            if (parent.getParent() instanceof View) {
                parent = (View) parent.getParent();
            } else {
                break;
            }
        }
        
        // Apply appropriate color based on theme and whether it's in a card
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        if (isInsideCardView) {
            // Text inside cards should be black for default/blue themes, white for dark theme
            if (currentTheme == ThemeManager.THEME_DARK) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                // For default and blue themes, use black text on cards
                textView.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        } else {
            // Normal text (not in cards) follows the main theme
            if (currentTheme == ThemeManager.THEME_DARK) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                textView.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }
    }
    
    /**
     * Apply appropriate background color to a CardView based on the current theme
     */
    private static void applyCardColorToView(CardView cardView, Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        int cardColor;
        if (currentTheme == ThemeManager.THEME_DARK) {
            cardColor = ContextCompat.getColor(context, R.color.dark_card_background);
        } else {
            cardColor = ContextCompat.getColor(context, R.color.light_card_background);
        }
        
        cardView.setCardBackgroundColor(cardColor);
        
        // Set text colors based on theme
        if (currentTheme == ThemeManager.THEME_DARK) {
            // For dark theme, forcefully apply white text to all child TextViews
            forceTextColor(cardView, context, ContextCompat.getColor(context, R.color.white));
        } else {
            // For light themes (default and blue), forcefully apply black text to all child TextViews
            forceTextColor(cardView, context, ContextCompat.getColor(context, R.color.black));
        }
    }
    
    /**
     * Force a specific text color on all TextViews in a view hierarchy
     */
    private static void forceTextColor(View view, Context context, int color) {
        if (view instanceof TextView && !isButtonOrSpecialView(view)) {
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                forceTextColor(child, context, color);
            }
        }
    }
    
    /**
     * Check if a view is a button or special view that should maintain its own colors
     */
    private static boolean isButtonOrSpecialView(View view) {
        return view instanceof android.widget.Button || 
               view instanceof com.google.android.material.button.MaterialButton;
    }
    
    /**
     * Check if a TextView should be skipped when applying colors
     * (Used for buttons, special elements that have their own styling)
     */
    private static boolean isSpecialTextView(TextView textView) {
        // Skip elements with IDs that should maintain their own styling
        int id = textView.getId();
        
        // For now, return false to apply colors to all TextViews
        // We might want to exclude some special cases in the future
        return false;
    }

    /**
     * Apply appropriate colors to all SearchViews in a view hierarchy
     * 
     * @param rootView The root view to traverse
     * @param context The context to use for getting colors
     */
    public static void applySearchViewColors(View rootView, Context context) {
        if (rootView instanceof androidx.appcompat.widget.SearchView) {
            applyColorsToSearchView((androidx.appcompat.widget.SearchView) rootView, context);
        } else if (rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                applySearchViewColors(child, context);
            }
        }
    }
    
    /**
     * Apply colors to a SearchView
     */
    private static void applyColorsToSearchView(androidx.appcompat.widget.SearchView searchView, Context context) {
        ThemeManager themeManager = ThemeManager.getInstance(context);
        int currentTheme = themeManager.getCurrentTheme();
        
        // Get the appropriate text color based on theme
        int textColor;
        if (currentTheme == ThemeManager.THEME_DARK) {
            textColor = ContextCompat.getColor(context, R.color.dark_text_primary);
        } else {
            textColor = ContextCompat.getColor(context, R.color.light_text_primary);
        }
        
        // Apply text color to search text
        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
        
        // Find EditText and set its color
        View editTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (editTextView instanceof TextView) {
            ((TextView) editTextView).setTextColor(textColor);
            ((TextView) editTextView).setHintTextColor(textColor);
        }
    }

    /**
     * Apply appropriate colors to icons
     */
    private static void applyIconColors(View rootView, Context context) {
        if (rootView instanceof ImageView) {
            ImageView imageView = (ImageView) rootView;
            
            // Check if this is a navigation or search icon that should be tinted
            if (isNavigationOrSearchIcon(imageView)) {
                ThemeManager themeManager = ThemeManager.getInstance(context);
                int currentTheme = themeManager.getCurrentTheme();
                
                // Get the appropriate icon tint based on theme
                int tintColor;
                if (currentTheme == ThemeManager.THEME_DARK) {
                    tintColor = ContextCompat.getColor(context, R.color.dark_text_primary);
                } else {
                    tintColor = ContextCompat.getColor(context, R.color.light_text_primary);
                }
                
                ViewCompat.setBackgroundTintList(imageView, ColorStateList.valueOf(tintColor));
            }
        } else if (rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                applyIconColors(child, context);
            }
        }
    }
    
    /**
     * Check if an ImageView is likely a navigation or search icon
     */
    private static boolean isNavigationOrSearchIcon(ImageView imageView) {
        // Look for specific content descriptions often used for navigation icons
        CharSequence contentDesc = imageView.getContentDescription();
        if (contentDesc != null) {
            String desc = contentDesc.toString().toLowerCase();
            return desc.contains("back") || 
                   desc.contains("home") || 
                   desc.contains("up") || 
                   desc.contains("menu") ||
                   desc.contains("search") ||
                   desc.contains("navigate");
        }
        return false;
    }
} 