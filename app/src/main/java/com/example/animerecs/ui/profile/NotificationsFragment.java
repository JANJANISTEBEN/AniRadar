package com.example.animerecs.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.animerecs.databinding.FragmentNotificationsBinding;
import com.google.android.material.snackbar.Snackbar;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        
        setupClickListeners();
        loadPreferences();
    }
    
    private void setupClickListeners() {
        // Back button
        binding.backButton.setOnClickListener(v -> navController.navigateUp());
        
        // Save button
        binding.saveButton.setOnClickListener(v -> savePreferences());
        
        // Toggle dependencies for push notifications
        binding.pushNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                // Disable all content notification switches when push is off
                binding.animeUpdatesSwitch.setEnabled(false);
                binding.mangaUpdatesSwitch.setEnabled(false);
                binding.recommendationsSwitch.setEnabled(false);
                
                // Show a message explaining
                Snackbar.make(binding.getRoot(), 
                    "Content notifications disabled when push notifications are off", 
                    Snackbar.LENGTH_SHORT).show();
            } else {
                // Re-enable content notification switches
                binding.animeUpdatesSwitch.setEnabled(true);
                binding.mangaUpdatesSwitch.setEnabled(true);
                binding.recommendationsSwitch.setEnabled(true);
            }
        });
    }
    
    private void loadPreferences() {
        // Here we would normally load preferences from SharedPreferences
        // For this demo, we'll just set default values
        binding.pushNotificationsSwitch.setChecked(true);
        binding.emailNotificationsSwitch.setChecked(true);
        binding.animeUpdatesSwitch.setChecked(true);
        binding.mangaUpdatesSwitch.setChecked(true);
        binding.recommendationsSwitch.setChecked(true);
    }
    
    private void savePreferences() {
        // Here we would normally save to SharedPreferences
        boolean pushNotifications = binding.pushNotificationsSwitch.isChecked();
        boolean emailNotifications = binding.emailNotificationsSwitch.isChecked();
        boolean animeUpdates = binding.animeUpdatesSwitch.isChecked();
        boolean mangaUpdates = binding.mangaUpdatesSwitch.isChecked();
        boolean recommendations = binding.recommendationsSwitch.isChecked();
        
        // Show a success message
        Toast.makeText(requireContext(), "Notification preferences saved", Toast.LENGTH_SHORT).show();
        
        // Navigate back after saving
        navController.navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 