package com.example.animerecs.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.animerecs.R;
import com.example.animerecs.databinding.FragmentSettingsBinding;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        
        setupClickListeners();
        loadSettings();
    }
    
    private void setupClickListeners() {
        // Back button
        binding.backButton.setOnClickListener(v -> navController.navigateUp());
        
        // Change password
        binding.changePasswordOption.setOnClickListener(v -> {
            // Show a temporary message since we haven't implemented this yet
            Snackbar.make(binding.getRoot(), "Change Password functionality coming soon", Snackbar.LENGTH_SHORT).show();
        });
        
        // Email preferences
        binding.emailPreferencesOption.setOnClickListener(v -> {
            // Show a temporary message since we haven't implemented this yet
            Snackbar.make(binding.getRoot(), "Email Preferences functionality coming soon", Snackbar.LENGTH_SHORT).show();
        });
        
        // Clear cache
        binding.clearCacheOption.setOnClickListener(v -> {
            // Simulate clearing cache with a delay
            binding.clearCacheOption.setEnabled(false);
            binding.getRoot().postDelayed(() -> {
                binding.clearCacheOption.setEnabled(true);
                Snackbar.make(binding.getRoot(), "Cache cleared successfully", Snackbar.LENGTH_SHORT).show();
            }, 800);
        });
        
        // Save button
        binding.saveButton.setOnClickListener(v -> saveSettings());
    }
    
    private void loadSettings() {
        // Here we would normally load settings from SharedPreferences
        // For this demo, we'll just set default values
        binding.autoPlaySwitch.setChecked(false);
        binding.wifiOnlySwitch.setChecked(true);
    }
    
    private void saveSettings() {
        // Here we would normally save to SharedPreferences
        boolean autoPlay = binding.autoPlaySwitch.isChecked();
        boolean wifiOnly = binding.wifiOnlySwitch.isChecked();
        
        // Show a success message
        Toast.makeText(requireContext(), "Settings saved successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate back after saving
        navController.navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 