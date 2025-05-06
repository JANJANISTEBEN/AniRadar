package com.example.animerecs;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.animerecs.data.repository.ThemeManager;
import com.example.animerecs.databinding.ActivityMainBinding;
import com.example.animerecs.ui.login.LoginActivity;
import com.example.animerecs.util.ColorUtils;
import com.example.animerecs.util.ThemeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize theme manager and apply theme before calling super.onCreate
        ThemeManager themeManager = ThemeManager.getInstance(this);
        themeManager.initializeTheme();
        
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply theme colors to all views
        applyThemeColors();
        
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        
        // Check if user is logged in, if not redirect to login page
        if (firebaseAuth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        // Get the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        // Get the NavController from the NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            // Set up the nav configuration with all top-level destinations
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_anime, R.id.navigation_manga,
                    R.id.navigation_bookmarks, R.id.navigation_profile)
                    .build();
            
            // Find the bottomNavigationView
            BottomNavigationView navView = binding.navView;
            
            // Set up manual handling for menu items to ensure explicit navigation
            navView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                Log.d(TAG, "Navigation item selected: " + item.getTitle() + " (ID: " + itemId + ")");
                
                // Create nav options to avoid fragmenting the back stack
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_home, false)
                        .setLaunchSingleTop(true)
                        .build();
                
                try {
                    // Navigate to the specific destination based on the menu item ID
                    if (itemId == R.id.navigation_home) {
                        Log.d(TAG, "Navigating to Home Fragment");
                        navController.navigate(R.id.navigation_home, null, navOptions);
                        return true;
                    } else if (itemId == R.id.navigation_anime) {
                        Log.d(TAG, "Navigating to Anime Fragment");
                        navController.navigate(R.id.navigation_anime, null, navOptions);
                        return true;
                    } else if (itemId == R.id.navigation_manga) {
                        Log.d(TAG, "Navigating to Manga Fragment");
                        navController.navigate(R.id.navigation_manga, null, navOptions);
                        return true;
                    } else if (itemId == R.id.navigation_bookmarks) {
                        Log.d(TAG, "Navigating to Bookmarks Fragment");
                        navController.navigate(R.id.navigation_bookmarks, null, navOptions);
                        return true;
                    } else if (itemId == R.id.navigation_profile) {
                        Log.d(TAG, "Navigating to Profile Fragment");
                        navController.navigate(R.id.navigation_profile, null, navOptions);
                        return true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Navigation error: " + e.getMessage());
                }
                
                return false;
            });
            
            // Add a listener to track navigation events for debugging
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                Log.d(TAG, "Navigation to: " + destination.getLabel() + " (ID: " + destination.getId() + ")");
                
                // Ensure the bottom navigation view reflects the current destination
                MenuItem menuItem = null;
                if (destination.getId() == R.id.navigation_home) {
                    menuItem = navView.getMenu().findItem(R.id.navigation_home);
                } else if (destination.getId() == R.id.navigation_anime) {
                    menuItem = navView.getMenu().findItem(R.id.navigation_anime);
                } else if (destination.getId() == R.id.navigation_manga) {
                    menuItem = navView.getMenu().findItem(R.id.navigation_manga);
                } else if (destination.getId() == R.id.navigation_bookmarks) {
                    menuItem = navView.getMenu().findItem(R.id.navigation_bookmarks);
                } else if (destination.getId() == R.id.navigation_profile) {
                    menuItem = navView.getMenu().findItem(R.id.navigation_profile);
                }
                
                // Update the selected item without triggering the listener
                if (menuItem != null && !menuItem.isChecked()) {
                    menuItem.setChecked(true);
                }
            });
            
            // Ensure we start at the home fragment
            if (savedInstanceState == null) {
                navController.navigate(R.id.navigation_home);
            }
        }
    }
    
    /**
     * Apply appropriate colors to views based on the current theme
     */
    private void applyThemeColors() {
        // Apply theme colors to all views using the new ColorUtils
        ColorUtils.applyThemeColors(binding.getRoot(), this);
        
        // Apply specific colors to bottom navigation
        BottomNavigationView navView = binding.navView;
        if (navView != null) {
            // Ensure the navigation bar adapts to the theme
            int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isDarkMode = nightMode == Configuration.UI_MODE_NIGHT_YES;
            
            // Set navigation bar translucent elevation effect
            navView.setElevation(8f);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Reapply colors when configuration changes (e.g., dark mode toggle)
        applyThemeColors();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh theme colors each time activity is resumed
        applyThemeColors();
    }
    
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Force refreshes the current fragment (if it's AnimeFragment)
     * This can be called when filters are applied to ensure a clean UI refresh
     */
    public void refreshCurrentFragment() {
        try {
            if (navController == null) {
                Log.e(TAG, "NavController is null");
                return;
            }
            
            NavDestination currentDestination = navController.getCurrentDestination();
            
            if (currentDestination != null && currentDestination.getId() == R.id.navigation_anime) {
                Log.d(TAG, "Refreshing anime fragment");
                
                // Get the NavHostFragment
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
                
                if (navHostFragment == null) {
                    Log.e(TAG, "NavHostFragment is null");
                    return;
                }
                
                // Get the child fragment manager (which contains the current fragment)
                androidx.fragment.app.FragmentManager childFragmentManager = navHostFragment.getChildFragmentManager();
                
                // Find the current fragment
                androidx.fragment.app.Fragment currentFragment = childFragmentManager.getPrimaryNavigationFragment();
                
                if (currentFragment != null) {
                    // Use the Fragment Transaction API to refresh the current fragment
                    childFragmentManager.beginTransaction()
                            .detach(currentFragment)
                            .attach(currentFragment)
                            .commitNow();
                    
                    Log.d(TAG, "Anime fragment refreshed");
                } else {
                    Log.e(TAG, "Current fragment is null");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing fragment: " + e.getMessage());
            e.printStackTrace();
        }
    }
}   