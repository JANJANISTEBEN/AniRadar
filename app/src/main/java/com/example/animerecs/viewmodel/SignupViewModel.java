package com.example.animerecs.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.animerecs.data.model.User;
import com.example.animerecs.data.repository.AuthRepository;

public class SignupViewModel extends ViewModel {
    private AuthRepository authRepository;
    
    public SignupViewModel() {
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
    
    public void register(String name, String email, String password, String birthday) {
        authRepository.register(name, email, password, birthday);
    }
} 