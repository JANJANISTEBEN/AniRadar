package com.example.animerecs.ui.forgotpassword;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.animerecs.databinding.ActivityForgotPasswordBinding;
import com.example.animerecs.viewmodel.LoginViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        // Send reset link button click listener
        binding.btnSendReset.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            
            if (email.isEmpty() || !email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Send password reset email
            loginViewModel.sendPasswordResetEmail(email);
        });

        // Back to login click listener
        binding.tvBackToLogin.setOnClickListener(v -> finish());
    }
    
    private void observeViewModel() {
        // Observe loading state
        loginViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnSendReset.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error messages
        loginViewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                
                // If message indicates success, finish the activity
                if (message.contains("sent")) {
                    finish();
                }
            }
        });
    }
} 