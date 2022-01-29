package com.example.expensetracker.ui.auth;

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
import androidx.navigation.Navigation;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentSignupBinding;

public class SignUpFragment extends Fragment {
    private FragmentSignupBinding binding;
    SignUpViewModel signUpViewModel;
    private Boolean validInputs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        signUpViewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fsContinueButton.setOnClickListener(b -> {
            validInputs = true;
            if (TextUtils.isEmpty(binding.fsFirstNameEditText.getText())) {
                binding.fsFirstNameEditText.setError("First name is required!");
                validInputs = false;
            }

            if (TextUtils.isEmpty(binding.fsLastNameEditText.getText())) {
                binding.fsLastNameEditText.setError("Last name is required!");
                validInputs = false;
            }

            if (validInputs) {
                signUpViewModel.saveUserDetails(
                        binding.fsFirstNameEditText.getText().toString(),
                        binding.fsLastNameEditText.getText().toString()
                );

                Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_emailPasswordFragment);
            }
        });

        // TODO: deal with avatar (select photo & save in firebase database)
    }

}
