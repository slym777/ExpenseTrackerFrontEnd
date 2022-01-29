package com.example.expensetracker.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentProfileBinding;
import com.example.expensetracker.ui.auth.AuthenticationActivity;
import com.example.expensetracker.utils.SharedPreferencesUtils;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fpSignoutButton.setOnClickListener(b -> {
            SharedPreferencesUtils.clearProfileDetails();

            Toast.makeText(getContext(), "You have been signed out.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), AuthenticationActivity.class);
            startActivity(intent);
        });
    }
}