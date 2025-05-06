package com.example.animerecs.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.animerecs.data.repository.ThemeManager;
import com.example.animerecs.databinding.FragmentThemeBinding;
import com.example.animerecs.util.ColorUtils;
import com.example.animerecs.util.ThemeUtils;
import com.google.android.material.snackbar.Snackbar;

public class ThemeFragment extends Fragment {

    private FragmentThemeBinding binding;
    private NavController navController;
    private int selectedTheme = 0; // THEME_LIGHT = 0, THEME_DARK = 1
    private ThemeManager themeManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentThemeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        themeManager = ThemeManager.getInstance(requireContext());
        
        setupClickListeners();
        loadThemeSettings();
        applyThemeColors();
    }
    
    /**
     * Apply appropriate colors to views based on the current theme
     */
    private void applyThemeColors() {
        // Apply theme colors to all views
        ColorUtils.applyThemeColors(binding.getRoot(), requireContext());
        
        // Additional custom coloring for special elements
        customizeColors();
    }
    
    /**
     * Apply custom colors to specific elements
     */
    private void customizeColors() {
        // Ensure toolbar text has proper color
        if (binding.toolbar != null) {
            TextView toolbarTitle = binding.toolbar.findViewById(android.R.id.text1);
            if (toolbarTitle != null) {
                toolbarTitle.setTextColor(ThemeUtils.getPrimaryTextColor(requireContext()));
            }
        }
        
        // Ensure the explanation text has proper contrast
        binding.colorSchemeExplanation.setTextColor(ThemeUtils.getSecondaryTextColor(requireContext()));
    }
    
    private void setupClickListeners() {
        // Back button
        binding.backButton.setOnClickListener(v -> navController.navigateUp());
        
        // Save button
        binding.saveButton.setOnClickListener(v -> applyThemeSettings());
        
        // Theme selection
        binding.lightTheme.setOnClickListener(v -> {
            selectedTheme = ThemeManager.THEME_LIGHT;
            highlightSelectedTheme();
            Snackbar.make(binding.getRoot(), "Light theme selected", Snackbar.LENGTH_SHORT).show();
        });
        
        binding.darkTheme.setOnClickListener(v -> {
            selectedTheme = ThemeManager.THEME_DARK;
            highlightSelectedTheme();
            Snackbar.make(binding.getRoot(), "Dark theme selected", Snackbar.LENGTH_SHORT).show();
        });
    }
    
    private void loadThemeSettings() {
        // Load current theme
        selectedTheme = themeManager.getCurrentTheme();
        highlightSelectedTheme();
    }
    
    private void highlightSelectedTheme() {
        // Reset all elevations
        binding.lightTheme.setCardElevation(4f);
        binding.darkTheme.setCardElevation(4f);
        
        // Highlight selected one
        if (selectedTheme == ThemeManager.THEME_LIGHT) {
            binding.lightTheme.setCardElevation(12f);
        } else {
            binding.darkTheme.setCardElevation(12f);
        }
    }
    
    private void applyThemeSettings() {
        // Get theme name for message
        String themeName = (selectedTheme == ThemeManager.THEME_DARK) ? "Dark" : "Light";
        
        // Show success message before making changes
        Toast.makeText(requireContext(), 
                "Theme updated: " + themeName + " theme", 
                Toast.LENGTH_SHORT).show();
                
        // Navigate back before theme change to avoid navigation issues during recreation
        navController.navigateUp();
        
        // Get ThemeManager using the Activity context to ensure activity recreation works
        ThemeManager activityThemeManager = ThemeManager.getInstance(requireActivity());
        
        // Apply settings - these may recreate the activity
        activityThemeManager.setTheme(selectedTheme);
    }

    @Override
    public void onConfigurationChanged(@NonNull android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Reapply colors when configuration changes (e.g., dark mode toggle)
        if (binding != null) {
            applyThemeColors();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 