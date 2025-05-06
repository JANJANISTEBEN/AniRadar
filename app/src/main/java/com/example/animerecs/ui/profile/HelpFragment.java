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

import com.example.animerecs.databinding.FragmentHelpBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class HelpFragment extends Fragment {

    private FragmentHelpBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHelpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        
        // Set back button click listener
        binding.backButton.setOnClickListener(v -> navController.navigateUp());
        
        // Set up the report issue button to open a dialog
        binding.reportIssueButton.setOnClickListener(v -> {
            // Create a dialog with an input field
            final View dialogView = LayoutInflater.from(getContext())
                    .inflate(com.example.animerecs.R.layout.dialog_report_issue, null);
            
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Report an Issue")
                    .setView(dialogView)
                    .setPositiveButton("Submit", (dialog, which) -> {
                        TextInputEditText editText = dialogView.findViewById(com.example.animerecs.R.id.issueDescriptionEditText);
                        String issueDescription = editText.getText().toString().trim();
                        
                        if (!issueDescription.isEmpty()) {
                            // In a real app, send this to a server
                            Snackbar.make(binding.getRoot(), "Issue submitted successfully", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 