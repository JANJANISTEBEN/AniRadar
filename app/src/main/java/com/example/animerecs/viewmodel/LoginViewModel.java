package com.example.animerecs.viewmodel;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.data.model.User;
import com.example.animerecs.data.repository.AuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public class LoginViewModel extends ViewModel {
    private AuthRepository authRepository;
    
    public LoginViewModel() {
        authRepository = AuthRepository.getInstance();
    }
    
    public LiveData<User> getCurrentUser() {
        return authRepository.getCurrentUser();
    }
    
    public LiveData<Boolean> getIsLoading() {
        return authRepository.getIsLoading();
    }
    
    public LiveData<String> getErrorMessage() {
        return authRepository.getErrorMessage();
    }
    
    public void login(String email, String password) {
        authRepository.login(email, password);
    }
    
    public void initGoogleSignIn(Context context) {
        authRepository.configureGoogleSignIn(context);
    }
    
    public Intent getGoogleSignInIntent() {
        return authRepository.getGoogleSignInIntent();
    }
    
    public void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        authRepository.handleGoogleSignInResult(task);
    }
    
    public void sendPasswordResetEmail(String email) {
        authRepository.sendPasswordResetEmail(email);
    }
    
    public void logout() {
        authRepository.logout();
    }
} 