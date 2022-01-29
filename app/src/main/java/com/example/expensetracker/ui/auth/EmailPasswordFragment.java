package com.example.expensetracker.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.databinding.FragmentAddEmailPasswordBinding;

import timber.log.Timber;

public class EmailPasswordFragment extends Fragment {
    private SignUpViewModel signUpViewModel;
    private FragmentAddEmailPasswordBinding binding;
    private Boolean validInputs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        signUpViewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);
        binding = FragmentAddEmailPasswordBinding.inflate(inflater, container, false);
        validInputs = true;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText email = binding.faepEmailEditText;
        EditText phoneNumber = binding.faepPhoneNumberEditText;
        EditText password = binding.faepPasswordEditText;
        EditText confirmPassword = binding.faepPasswordConfirmEditText;

        binding.faepSignupButton.setOnClickListener(b -> {
            validInputs = true;
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            String phoneNumberPattern = "(^$|[0-9]{10})";

            if (TextUtils.isEmpty(email.getText())) {
                email.setError("Email is required!");
                validInputs = false;
            } else if (!email.getText().toString().trim().matches(emailPattern)) {
                email.setError("Invalid email address: wrong format.");
                validInputs = false;
            } else {
                signUpViewModel.email = email.getText().toString();
            }

            if (TextUtils.isEmpty(phoneNumber.getText())) {
                phoneNumber.setError("Phone number is required!");
                validInputs = false;
            } else if (!phoneNumber.getText().toString().matches(phoneNumberPattern)) {
                phoneNumber.setError("Invalid phone number: wrong format.");
                validInputs = false;
            } else {
                signUpViewModel.phoneNumber = phoneNumber.getText().toString();
            }

            if (TextUtils.isEmpty(password.getText())) {
                password.setError("Password is required!");
                validInputs = false;
            }

            if (TextUtils.isEmpty(confirmPassword.getText())) {
                confirmPassword.setError("The password confirmation is required!");
                validInputs = false;
            }

            if (password.getText().toString().equals(confirmPassword.getText().toString())){
                signUpViewModel.password = password.getText().toString();
            } else {
                confirmPassword.setError("Passwords do not match");
                validInputs = false;
            }

            if (validInputs) {
                signUpViewModel.signUp();
            }
        });

        signUpViewModel.signUpState.subscribe(bool -> {
            if (bool) {
                Intent intent = new Intent(getContext(), AuthenticationActivity.class);
                startActivity(intent);
            }
        }, Timber::e);
    }
}
