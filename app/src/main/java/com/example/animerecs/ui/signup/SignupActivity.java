package com.example.animerecs.ui.signup;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.animerecs.R;
import com.example.animerecs.databinding.ActivitySignupBinding;
import com.example.animerecs.viewmodel.SignupViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private SignupViewModel signupViewModel;
    private Calendar calendar;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);
        
        calendar = Calendar.getInstance();
        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        // Birthday field click listener - show date picker
        binding.etBirthday.setOnClickListener(v -> showDatePicker());

        // Register button click listener
        binding.btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                // Register user with Firebase
                String name = binding.etName.getText().toString().trim();
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();
                String birthday = binding.etBirthday.getText().toString().trim();
                
                signupViewModel.register(name, email, password, birthday);
            }
        });

        // Login here click listener
        binding.tvLoginHere.setOnClickListener(v -> finish());
    }
    
    private void observeViewModel() {
        // Observe loading state
        signupViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnRegister.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error messages
        signupViewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        // Observe current user
        signupViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // User registered successfully, navigate back to login
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            R.style.DatePickerTheme,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateBirthdayField();
            },
            calendar.get(Calendar.YEAR) - 18, // Default to 18 years ago
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set max date to current date (no future birthdays)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        
        datePickerDialog.show();
    }

    private void updateBirthdayField() {
        binding.etBirthday.setText(dateFormatter.format(calendar.getTime()));
    }

    private boolean validateInputs() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        String birthday = binding.etBirthday.getText().toString().trim();
        
        // Basic validation
        if (name.isEmpty()) {
            binding.tilName.setError("Please enter your name");
            return false;
        } else {
            binding.tilName.setError(null);
        }
        
        if (email.isEmpty() || !email.contains("@")) {
            binding.tilEmail.setError("Please enter a valid email address");
            return false;
        } else {
            binding.tilEmail.setError(null);
        }
        
        if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            return false;
        } else {
            binding.tilPassword.setError(null);
        }
        
        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            return false;
        } else {
            binding.tilConfirmPassword.setError(null);
        }
        
        if (birthday.isEmpty()) {
            binding.tilBirthday.setError("Please select your birthday");
            return false;
        } else {
            binding.tilBirthday.setError(null);
        }
        
        return true;
    }
} 