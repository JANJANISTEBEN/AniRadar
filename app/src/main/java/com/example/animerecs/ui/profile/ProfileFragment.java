package com.example.animerecs.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.animerecs.R;
import com.example.animerecs.data.model.Bookmark;
import com.example.animerecs.data.model.User;
import com.example.animerecs.data.repository.AuthRepository;
import com.example.animerecs.data.repository.BookmarkRepository;
import com.example.animerecs.data.repository.ThemeManager;
import com.example.animerecs.data.repository.UserRepository;
import com.example.animerecs.databinding.FragmentProfileBinding;
import com.example.animerecs.ui.bookmarks.BookmarksViewModel;
import com.example.animerecs.ui.login.LoginActivity;
import com.example.animerecs.util.ColorUtils;
import com.example.animerecs.util.ThemeUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private NavController navController;
    private BookmarkRepository bookmarkRepository;
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private BookmarksViewModel bookmarksViewModel;
    private FirebaseFirestore firestore;
    private ThemeManager themeManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        bookmarkRepository = new BookmarkRepository();
        userRepository = new UserRepository();
        authRepository = AuthRepository.getInstance();
        bookmarksViewModel = new ViewModelProvider(this).get(BookmarksViewModel.class);
        firestore = FirebaseFirestore.getInstance();
        themeManager = ThemeManager.getInstance(requireContext());
        
        // Setup profile data
        setupProfileData();
        
        // Load bookmark counts
        loadBookmarkCounts();
        
        // Setup click listeners
        setupClickListeners();
        
        // Apply theme colors to all views
        applyThemeColors();
    }

    /**
     * Apply appropriate colors based on the current theme
     */
    private void applyThemeColors() {
        // Use ColorUtils to apply colors to all TextViews and CardViews
        ColorUtils.applyThemeColors(binding.getRoot(), requireContext());
        
        // Ensure the header section has proper contrast
        ensureHeaderTextContrast();
        
        // Specifically handle certain UI elements
        applyThemeColorsToSections();
    }
    
    /**
     * Apply theme colors to specific section elements
     */
    private void applyThemeColorsToSections() {
        // Apply to section titles
        if (binding.getRoot() instanceof ViewGroup) {
            ViewGroup root = (ViewGroup) binding.getRoot();
            
            // Find and apply colors to all TextView elements with "About" text
            for (int i = 0; i < root.getChildCount(); i++) {
                View child = root.getChildAt(i);
                if (child instanceof ViewGroup) {
                    findAndColorSectionTitles((ViewGroup) child);
                }
            }
        }
        
        // Apply colors to bookmark stats section
        int primaryColor = ThemeUtils.getPrimaryColor(requireContext());
        binding.animeBookmarksCountTextView.setTextColor(primaryColor);
        binding.mangaBookmarksCountTextView.setTextColor(primaryColor);
    }
    
    /**
     * Find and apply colors to section title TextViews
     */
    private void findAndColorSectionTitles(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            
            // If it's a TextView with text matching section titles
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                CharSequence text = textView.getText();
                
                if (text != null && (
                    text.equals("About") || 
                    text.equals("Appearance") ||
                    text.equals("Settings")
                )) {
                    // It's a section title, apply primary color
                    textView.setTextColor(ThemeUtils.getPrimaryColor(requireContext()));
                }
            }
            
            // Recursively check children
            if (child instanceof ViewGroup) {
                findAndColorSectionTitles((ViewGroup) child);
            }
        }
    }
    
    /**
     * Ensure the header text has proper contrast with its background
     */
    private void ensureHeaderTextContrast() {
        // Get current theme to determine text color
        ThemeManager themeManager = ThemeManager.getInstance(requireContext());
        int currentTheme = themeManager.getCurrentTheme();
        
        if (currentTheme == ThemeManager.THEME_DARK) {
            // For dark theme, use light text for contrast
            binding.usernameTextView.setTextColor(getResources().getColor(R.color.dark_text_primary));
            binding.emailTextView.setTextColor(getResources().getColor(R.color.dark_text_secondary));
        } else {
            // For light theme, use dark text
            binding.usernameTextView.setTextColor(getResources().getColor(R.color.light_text_primary));
            binding.emailTextView.setTextColor(getResources().getColor(R.color.light_text_secondary));
        }
    }

    private void setupProfileData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User localUser = userRepository.getCurrentUser();
        
        if (currentUser != null) {
            // Set profile image
            if (currentUser.getPhotoUrl() != null) {
                // Use Glide to load profile image
                com.bumptech.glide.Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .into(binding.profileImageView);
            } else if (localUser != null && localUser.getProfileImageUrl() != null) {
                com.bumptech.glide.Glide.with(this)
                    .load(localUser.getProfileImageUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .into(binding.profileImageView);
            }
            
            // Set email
            String email = currentUser.getEmail();
            if (email != null && !email.isEmpty()) {
                binding.emailTextView.setText(email);
            } else {
                binding.emailTextView.setText(localUser != null ? localUser.getEmail() : "No email provided");
            }
            
            // Set username
            String username = currentUser.getDisplayName();
            if (username != null && !username.isEmpty()) {
                binding.usernameTextView.setText(username);
            } else {
                // If Firebase Auth displayName is empty, try to get it from Firestore
                loadUserDataFromFirestore(currentUser.getUid());
            }
        } else {
            // Use local user data if Firebase user is null
            if (localUser != null) {
                binding.usernameTextView.setText(localUser.getDisplayName());
                binding.emailTextView.setText(localUser.getEmail());
                
                if (localUser.getProfileImageUrl() != null) {
                    com.bumptech.glide.Glide.with(this)
                        .load(localUser.getProfileImageUrl())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(binding.profileImageView);
                }
            } else {
                // Redirect to login if not authenticated
                redirectToLogin();
            }
        }
    }
    
    private void loadUserDataFromFirestore(String userId) {
        // Show loading state
        binding.usernameTextView.setText("Loading...");
        
        // Get user data from Firestore
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Get the user's name from Firestore
                    String name = documentSnapshot.getString("name");
                    if (name != null && !name.isEmpty()) {
                        binding.usernameTextView.setText(name);
                        
                        // Update local user repository
                        User localUser = userRepository.getCurrentUser();
                        if (localUser != null) {
                            localUser.setDisplayName(name);
                            userRepository.updateUser(localUser);
                        }
                    } else {
                        binding.usernameTextView.setText("User");
                    }
                } else {
                    binding.usernameTextView.setText("User");
                    Log.d(TAG, "No user document found in Firestore");
                }
            })
            .addOnFailureListener(e -> {
                binding.usernameTextView.setText("User");
                Log.e(TAG, "Error loading user data: " + e.getMessage());
            });
    }
    
    private void loadBookmarkCounts() {
        // Initialize with zeros
        binding.animeBookmarksCountTextView.setText("0");
        binding.mangaBookmarksCountTextView.setText("0");
        
        // Observe anime bookmarks
        bookmarksViewModel.getAnimeBookmarks().observe(getViewLifecycleOwner(), bookmarks -> {
            int count = bookmarks != null ? bookmarks.size() : 0;
            binding.animeBookmarksCountTextView.setText(String.valueOf(count));
        });
        
        // Observe manga bookmarks
        bookmarksViewModel.getMangaBookmarks().observe(getViewLifecycleOwner(), bookmarks -> {
            int count = bookmarks != null ? bookmarks.size() : 0;
            binding.mangaBookmarksCountTextView.setText(String.valueOf(count));
        });
    }

    private void setupClickListeners() {
        // Terms of Service
        binding.termsLayout.setOnClickListener(v -> 
            navController.navigate(R.id.action_navigation_profile_to_termsFragment));
        
        // Help & Support
        binding.helpSupportLayout.setOnClickListener(v -> 
            navController.navigate(R.id.action_navigation_profile_to_helpFragment));
        
        // Theme settings
        binding.themeSettingsLayout.setOnClickListener(v -> 
            navController.navigate(R.id.action_navigation_profile_to_themeFragment));
        
        // Logout
        binding.logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        try {
            // Disable the logout button to prevent multiple clicks
            binding.logoutButton.setEnabled(false);
            
            // Sign out using AuthRepository
            authRepository.logout();
            
            // Show success message
            android.widget.Toast.makeText(requireContext(), 
                "Logged out successfully", 
                android.widget.Toast.LENGTH_SHORT).show();
            
            // Use a safer approach to navigate to login
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            // Clear the activity stack to prevent going back to the app after logout
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        } catch (Exception e) {
            // Re-enable the button in case of failure
            binding.logoutButton.setEnabled(true);
            
            // Log the error and show a message
            Log.e(TAG, "Error during logout: " + e.getMessage());
            android.widget.Toast.makeText(requireContext(), 
                "Error logging out. Please try again.", 
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToLogin() {
        try {
            if (isAdded() && navController != null) {
                navController.navigate(R.id.action_navigation_profile_to_loginFragment);
            } else {
                // If navigation is not possible, start LoginActivity directly
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (isAdded()) {
                    requireActivity().finish();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error redirecting to login: " + e.getMessage());
            // If all else fails, try to start LoginActivity directly
            try {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (Exception ex) {
                Log.e(TAG, "Failed to start LoginActivity: " + ex.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        if (isAdded()) {
            setupProfileData();
            loadBookmarkCounts();
            
            // Re-apply text colors in case the theme has changed
            applyThemeColors();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 