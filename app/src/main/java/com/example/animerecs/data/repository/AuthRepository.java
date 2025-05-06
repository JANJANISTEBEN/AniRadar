package com.example.animerecs.data.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.animerecs.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private static AuthRepository instance;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;
    private final UserRepository userRepository;
    
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private static final int RC_SIGN_IN = 9001;

    private AuthRepository() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        
        // Initialize UserRepository
        userRepository = new UserRepository();
        
        // Check if user is already logged in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Create a user object with basic info from Firebase Auth
            User newUser = new User(
                user.getUid(),
                user.getEmail(), 
                user.getDisplayName()
            );
            
            // Update the UserRepository with the current user data
            userRepository.createOrUpdateUser(
                user.getUid(),
                user.getDisplayName(),
                user.getEmail()
            );
            
            // Update the LiveData
            currentUser.setValue(newUser);
            
            // Also try to get additional user data from Firestore
            loadUserFromFirestore(user.getUid());
        }
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Loads additional user data from Firestore
     * @param userId The user ID to load data for
     */
    private void loadUserFromFirestore(String userId) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Get user data from Firestore
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String bio = documentSnapshot.getString("bio");
                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                    
                    // If we have a name from Firestore, update the current user data
                    if (name != null && !name.isEmpty()) {
                        // Update UserRepository
                        userRepository.createOrUpdateUser(
                            userId,
                            name,
                            email,
                            bio,
                            profileImageUrl
                        );
                        
                        // Create a new User object with the updated data
                        User updatedUser = new User(
                            userId,
                            name,
                            email,
                            bio,
                            profileImageUrl
                        );
                        
                        // Update the LiveData
                        currentUser.setValue(updatedUser);
                    }
                }
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(TAG, "Error loading user data from Firestore: " + e.getMessage());
            });
    }

    public void login(String email, String password) {
        // Clear any previous error message first
        errorMessage.setValue("");
        
        // Validate inputs only when method is called (when login button is clicked)
        if (email == null || email.isEmpty() || !email.contains("@")) {
            errorMessage.setValue("Please enter a valid email address");
            return;
        }
        
        if (password == null || password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters");
            return;
        }
        
        isLoading.setValue(true);
        
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                isLoading.setValue(false);
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        // Create a basic user object
                        User user = new User(
                            firebaseUser.getUid(),
                            firebaseUser.getEmail(),
                            firebaseUser.getDisplayName()
                        );
                        
                        // Update the UserRepository
                        userRepository.createOrUpdateUser(
                            firebaseUser.getUid(),
                            firebaseUser.getDisplayName(),
                            firebaseUser.getEmail()
                        );
                        
                        // Update the LiveData
                        currentUser.setValue(user);
                        
                        // Load additional user data from Firestore
                        loadUserFromFirestore(firebaseUser.getUid());
                    }
                } else {
                    errorMessage.setValue(task.getException() != null ? 
                        task.getException().getMessage() : 
                        "Authentication failed.");
                }
            });
    }

    public void register(String name, String email, String password, String birthday) {
        isLoading.setValue(true);
        
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        // Store additional user info in Firestore
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("birthday", birthday);
                        userMap.put("createdAt", System.currentTimeMillis());
                        
                        DocumentReference userRef = firestore.collection("users").document(firebaseUser.getUid());
                        userRef.set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                // Create a new User object with the registered data
                                User user = new User(
                                    firebaseUser.getUid(),
                                    name,
                                    email
                                );
                                
                                // Update the UserRepository
                                userRepository.createOrUpdateUser(
                                    firebaseUser.getUid(),
                                    name,
                                    email
                                );
                                
                                // Update the LiveData
                                currentUser.setValue(user);
                                isLoading.setValue(false);
                            })
                            .addOnFailureListener(e -> {
                                errorMessage.setValue("Failed to create user profile.");
                                isLoading.setValue(false);
                            });
                    } else {
                        errorMessage.setValue("Error creating user.");
                        isLoading.setValue(false);
                    }
                } else {
                    errorMessage.setValue(task.getException() != null ? 
                        task.getException().getMessage() : 
                        "Registration failed.");
                    isLoading.setValue(false);
                }
            });
    }

    public void sendPasswordResetEmail(String email) {
        isLoading.setValue(true);
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                isLoading.setValue(false);
                if (task.isSuccessful()) {
                    errorMessage.setValue("Password reset email sent.");
                } else {
                    errorMessage.setValue(task.getException() != null ? 
                        task.getException().getMessage() : 
                        "Failed to send reset email.");
                }
            });
    }

    public void configureGoogleSignIn(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("799506047681-t14ethsnt2nqurikpqnv7td7ku5c8khi.apps.googleusercontent.com")
            .requestEmail()
            .build();
        
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public Intent getGoogleSignInIntent() {
        // Always sign out before starting the sign-in flow to ensure account selection
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
        return googleSignInClient.getSignInIntent();
    }

    public void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            isLoading.setValue(true);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            isLoading.setValue(false);
            errorMessage.setValue("Google sign in failed: " + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(task -> {
                isLoading.setValue(false);
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User(
                            firebaseUser.getUid(),
                            firebaseUser.getEmail(),
                            firebaseUser.getDisplayName()
                        );
                        
                        // Check if user already exists in Firestore
                        DocumentReference userRef = firestore.collection("users").document(firebaseUser.getUid());
                        userRef.get().addOnCompleteListener(firestoreTask -> {
                            if (firestoreTask.isSuccessful() && !firestoreTask.getResult().exists()) {
                                // New Google user, save to Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("name", firebaseUser.getDisplayName());
                                userMap.put("email", firebaseUser.getEmail());
                                userMap.put("createdAt", System.currentTimeMillis());
                                userMap.put("authProvider", "google");
                                
                                userRef.set(userMap).addOnFailureListener(e -> 
                                    errorMessage.setValue("Failed to create user profile."));
                            }
                            currentUser.setValue(user);
                        });
                    }
                } else {
                    errorMessage.setValue(task.getException() != null ? 
                        task.getException().getMessage() : 
                        "Google authentication failed.");
                }
            });
    }

    public void logout() {
        firebaseAuth.signOut();
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
        
        // Clear the UserRepository data
        userRepository.logout();
        
        // Update LiveData
        currentUser.setValue(null);
    }
} 