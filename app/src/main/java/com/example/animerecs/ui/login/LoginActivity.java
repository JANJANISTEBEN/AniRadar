package com.example.animerecs.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.animerecs.MainActivity;
import com.example.animerecs.R;
import com.example.animerecs.data.repository.ThemeManager;
import com.example.animerecs.databinding.ActivityLoginBinding;
import com.example.animerecs.ui.forgotpassword.ForgotPasswordActivity;
import com.example.animerecs.ui.signup.SignupActivity;
import com.example.animerecs.util.ThemeUtils;
import com.example.animerecs.viewmodel.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    private static final int RC_SIGN_IN = 9001;
    
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                loginViewModel.handleGoogleSignInResult(task);
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize theme before setting content view
        ThemeManager.getInstance(this).initializeTheme();
        
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Apply theme colors
        applyThemeColors();

        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        
        // Initialize Google Sign In
        loginViewModel.initGoogleSignIn(this);

        setupListeners();
        observeViewModel();
    }

    /**
     * Apply theme colors to UI elements
     */
    private void applyThemeColors() {
        // Update status bar color
        getWindow().setStatusBarColor(ThemeUtils.getPrimaryDarkColor(this));
        
        // Apply background color
        View rootView = binding.getRoot();
        if (rootView != null) {
            rootView.setBackgroundColor(ThemeUtils.getBackgroundColor(this));
        }
        
        // Apply colors to login button
        if (binding.btnLogin != null) {
            binding.btnLogin.setBackgroundColor(ThemeUtils.getPrimaryColor(this));
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh theme colors each time activity is resumed
        applyThemeColors();
    }

    private void setupListeners() {
        // Login button click listener
        binding.btnLogin.setOnClickListener(v -> {
            // Clear any previous errors first
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);
            
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            loginViewModel.login(email, password);
        });

        // Google login button click listener
        binding.btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = loginViewModel.getGoogleSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // Sign up text click listener
        binding.tvSignUp.setOnClickListener(v -> {
            // Navigate to sign up screen
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });

        // Forgot password text click listener
        binding.tvForgotPassword.setOnClickListener(v -> {
            // Navigate to forgot password screen
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        // Observe loading state
        loginViewModel.getIsLoading().observe(this, isLoading -> {
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnGoogleLogin.setEnabled(!isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe error messages - show them as toasts instead of field errors
        loginViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // Clear any field errors to avoid duplicate messages
                binding.tilEmail.setError(null);
                binding.tilPassword.setError(null);
                
                // Show error as toast
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Observe current user
        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // User logged in successfully, navigate to main screen
                Toast.makeText(this, "Login successful: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
} 